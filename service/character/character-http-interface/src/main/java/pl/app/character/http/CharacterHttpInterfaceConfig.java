package pl.app.character.http;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

public class CharacterHttpInterfaceConfig {
    @Bean
    CharacterQueryControllerHttpInterface votingQueryControllerHttpInterface(
            @Value("${app.service.character-service.base-url}") String votingServiceBaseUrl
    ) {
        WebClient webClient = WebClient.builder().baseUrl(votingServiceBaseUrl).build();
        WebClientAdapter adapter = WebClientAdapter.create(webClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();

        return factory.createClient(CharacterQueryControllerHttpInterface.class);
    }
}
