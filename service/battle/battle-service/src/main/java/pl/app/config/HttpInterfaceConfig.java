package pl.app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import pl.app.character.http.CharacterHttpInterfaceConfig;
import pl.app.character.http.MonsterHttpInterfaceConfig;
import pl.app.god.http.GodHttpInterfaceConfig;
import pl.app.item.http.ItemHttpInterfaceConfig;

@Configuration
@Import({
        GodHttpInterfaceConfig.class,
        CharacterHttpInterfaceConfig.class,
        ItemHttpInterfaceConfig.class,
        MonsterHttpInterfaceConfig.class
})
@PropertySource("classpath:services.properties")
public class HttpInterfaceConfig {
}
