package online.dbaltor.demoapi;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(info = @Info(title = "Restful Banking API demo"))
@SpringBootApplication
public class DemoAPIApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoAPIApplication.class, args);
    }
}
