package pl.app.character.http;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

public class CharacterHttpInterfaceConfig {
    @Bean
    HttpServiceProxyFactory itemServiceHttpServiceProxyFactory(
            @Value("${app.service.character-service.base-url}") String characterServiceBaseUrl
    ) {
        WebClient webClient = WebClient.builder().baseUrl(characterServiceBaseUrl).build();
        WebClientAdapter adapter = WebClientAdapter.create(webClient);
        return HttpServiceProxyFactory.builderFor(adapter).build();
    }

    @Bean
    CharacterQueryControllerHttpInterface characterQueryControllerHttpInterface(HttpServiceProxyFactory factory) {
        return factory.createClient(CharacterQueryControllerHttpInterface.class);
    }
    @Bean
    GodFamilyQueryControllerHttpInterface godFamilyQueryControllerHttpInterface(HttpServiceProxyFactory factory) {
        return factory.createClient(GodFamilyQueryControllerHttpInterface.class);
    }
}
