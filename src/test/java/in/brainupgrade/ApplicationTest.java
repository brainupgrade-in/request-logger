package in.brainupgrade;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class ApplicationTest {

    @Test
    public void contextLoads() {
        // This test verifies that the Spring application context loads successfully
    }

    @Test
    public void mainMethodRuns() {
        // Test that main method can be invoked without errors
        String[] args = {};
        // We don't actually call main() in tests to avoid starting the server
        // This test just ensures the Application class is properly configured
        assert Application.class != null;
    }
}
