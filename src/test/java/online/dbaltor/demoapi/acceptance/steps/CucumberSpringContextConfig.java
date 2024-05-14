package online.dbaltor.demoapi.acceptance.steps;

import io.cucumber.spring.CucumberContextConfiguration;
import online.dbaltor.demoapi.util.MyClock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class CucumberSpringContextConfig {
    @MockBean MyClock clock;

    @LocalServerPort protected int port;

    protected int getServerPort() {
        return port;
    }
}
