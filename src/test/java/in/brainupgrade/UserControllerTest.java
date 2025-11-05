package in.brainupgrade;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    private User testUser;
    private List<User> testUsers;

    @BeforeEach
    public void setup() {
        testUser = new User();
        testUser.setSessionID("test-session-123");
        testUser.setCallerIP("192.168.1.1");
        testUser.setOriginatingIP("10.0.0.1");
        testUser.setHost("localhost/127.0.0.1");
        testUser.setAccessTime(new Date());

        User testUser2 = new User();
        testUser2.setSessionID("test-session-456");
        testUser2.setCallerIP("192.168.1.2");
        testUser2.setOriginatingIP("10.0.0.2");
        testUser2.setHost("localhost/127.0.0.1");
        testUser2.setAccessTime(new Date());

        testUsers = Arrays.asList(testUser, testUser2);
    }

    @Test
    public void testSaveRequest_Success() throws Exception {
        when(userRepository.saveAndFlush(any(User.class))).thenReturn(testUser);

        mockMvc.perform(get("/")
                .header("X-FORWARDED-FOR", "10.0.0.1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.callerIP", notNullValue()))
                .andExpect(jsonPath("$.sessionID", notNullValue()))
                .andExpect(jsonPath("$.accessTime", notNullValue()));
    }

    @Test
    public void testSaveRequest_WithForwardedHeader() throws Exception {
        when(userRepository.saveAndFlush(any(User.class))).thenReturn(testUser);

        mockMvc.perform(get("/")
                .header("X-FORWARDED-FOR", "203.0.113.1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.originatingIP", is("203.0.113.1")));
    }

    @Test
    public void testSaveRequest_WithoutForwardedHeader() throws Exception {
        when(userRepository.saveAndFlush(any(User.class))).thenReturn(testUser);

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.callerIP", notNullValue()));
    }

    @Test
    public void testGetAll_Success() throws Exception {
        when(userRepository.findAll()).thenReturn(testUsers);

        mockMvc.perform(get("/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].sessionID", notNullValue()))
                .andExpect(jsonPath("$[1].sessionID", notNullValue()));
    }

    @Test
    public void testGetAll_EmptyList() throws Exception {
        when(userRepository.findAll()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void testHealthCheck() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;charset=UTF-8"))
                .andExpect(content().string("OK"));
    }

    @Test
    public void testGetVersion_WithEnvironmentVariables() throws Exception {
        mockMvc.perform(get("/version"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;charset=UTF-8"))
                .andExpect(content().string(containsString("Version: Build ID")))
                .andExpect(content().string(containsString("Commit ID")));
    }

    @Test
    public void testGetVersion_ResponseFormat() throws Exception {
        mockMvc.perform(get("/version"))
                .andExpect(status().isOk())
                .andExpect(content().string(matchesPattern("Version: Build ID - .*\\tCommit ID - .*")));
    }
}
