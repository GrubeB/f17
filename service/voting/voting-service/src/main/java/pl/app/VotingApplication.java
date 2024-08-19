package pl.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@ConfigurationPropertiesScan
public class VotingApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(VotingApplication.class, args);
    }
}
