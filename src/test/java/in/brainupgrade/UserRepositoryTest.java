package in.brainupgrade;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testSaveUser() {
        // Create test user
        User user = new User();
        user.setSessionID("test-session-789");
        user.setCallerIP("192.168.1.100");
        user.setOriginatingIP("10.0.0.100");
        user.setHost("testhost/127.0.0.1");
        user.setAccessTime(new Date());

        // Save user
        User savedUser = userRepository.saveAndFlush(user);

        // Verify save
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getSessionID()).isEqualTo("test-session-789");
        assertThat(savedUser.getCallerIP()).isEqualTo("192.168.1.100");
    }

    @Test
    public void testFindAllUsers() {
        // Create and save test users
        User user1 = new User();
        user1.setSessionID("session-1");
        user1.setCallerIP("192.168.1.1");
        user1.setAccessTime(new Date(System.currentTimeMillis() - 1000));
        entityManager.persist(user1);

        User user2 = new User();
        user2.setSessionID("session-2");
        user2.setCallerIP("192.168.1.2");
        user2.setAccessTime(new Date());
        entityManager.persist(user2);

        entityManager.flush();

        // Find all users
        List<User> users = userRepository.findAll();

        // Verify results
        assertThat(users).hasSize(2);
        assertThat(users).extracting(User::getSessionID)
                .containsExactlyInAnyOrder("session-1", "session-2");
    }

    @Test
    public void testFindAllUsers_EmptyDatabase() {
        // Find all when database is empty
        List<User> users = userRepository.findAll();

        // Verify empty list
        assertThat(users).isEmpty();
    }
}
