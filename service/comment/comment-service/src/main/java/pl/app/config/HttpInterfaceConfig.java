package pl.app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import pl.app.comment.VotingHttpInterfaceConfig;

@Configuration
@Import(VotingHttpInterfaceConfig.class)
@PropertySource("classpath:services.properties")
public class HttpInterfaceConfig {
}
