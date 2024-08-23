package pl.app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import pl.app.item.http.ItemHttpInterfaceConfig;

@Configuration
@Import({ItemHttpInterfaceConfig.class})
@PropertySource("classpath:services.properties")
public class HttpInterfaceConfig {
}
