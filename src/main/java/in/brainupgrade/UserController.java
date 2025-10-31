package in.brainupgrade;

import java.net.InetAddress;
import java.util.Date;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

	@Autowired
	UserRepository userRepository;

	@GetMapping(value = "/")
	public User saveRequest(HttpServletRequest request, HttpServletResponse response) {

		HttpSession session = request.getSession(true);
		String sessionID = session.getId();
		String callerIP = request.getRemoteAddr();
//		String host = request.getServerName();
		String originatingIP = request.getHeader("X-FORWARDED-FOR");

		User user = new User();
		user.setAccessTime(new Date());
		user.setCallerIP(callerIP);
		user.setSessionID(sessionID);
		user.setOriginatingIP(originatingIP);
		try {
			InetAddress ip = InetAddress.getLocalHost();
			user.setHost("" + ip);
			userRepository.saveAndFlush(user);
		} catch (Exception exe) {
			// ignore
		}
		return user;
	}
	@GetMapping(value = "/all")
	public List<User> getAll(HttpServletRequest request, HttpServletResponse response) {

		return userRepository.findAll();
	}

	/**
	* Health endpoint for test and monitoring purpose
	*/
	@GetMapping("/health")
	public String checkHealth() {
	// Implement your health check logic here	
	return "OK"; // A simple string indicating health status
	}	
	/**
	* System version
	*/
	@GetMapping(value = "/version")
	public String getVersion() {
	
		String buildId = System.getenv("BUILD_ID");
		String commitId = System.getenv("GIT_COMMIT_ID");
		return "Version: Build ID - "+buildId+"\tCommit ID - "+commitId;
	// return "Build ID: " + (buildId != null ? buildId : "unknown")+"\tCommit ID: " + (commitId != null ? commitId : "unknown"); // A simple string indicating health status
	}
}
