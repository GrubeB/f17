package pl.app.character.http;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

public class CharacterHttpInterfaceConfig {
    @Value("${app.service.character-service.base-url}")
    private String characterServiceBaseUrl;
    HttpServiceProxyFactory factory() {
        WebClient webClient = WebClient.builder()
                .baseUrl(characterServiceBaseUrl)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
        WebClientAdapter adapter = WebClientAdapter.create(webClient);
        return HttpServiceProxyFactory.builderFor(adapter)
                .build();
    }
    @Bean
    CharacterQueryControllerHttpInterface characterQueryControllerHttpInterface() {
        return factory().createClient(CharacterQueryControllerHttpInterface.class);
    }
    @Bean
    GodFamilyQueryControllerHttpInterface godFamilyQueryControllerHttpInterface() {
        return factory().createClient(GodFamilyQueryControllerHttpInterface.class);
    }
    @Bean
    CharacterWithGearQueryControllerHttpInterface characterWithGearQueryControllerHttpInterface() {
        return factory().createClient(CharacterWithGearQueryControllerHttpInterface.class);
    }
    @Bean
    GodFamilyWithGearQueryControllerHttpInterface godFamilyWithGearQueryControllerHttpInterface() {
        return factory().createClient(GodFamilyWithGearQueryControllerHttpInterface.class);
    }
}
