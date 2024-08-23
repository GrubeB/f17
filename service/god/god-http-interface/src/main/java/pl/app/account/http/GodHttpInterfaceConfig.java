package pl.app.account.http;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

public class GodHttpInterfaceConfig {

    @Bean
    HttpServiceProxyFactory godServiceHttpServiceProxyFactory(
            @Value("${app.service.god-service.base-url}") String godServiceBaseUrl
    ) {
        WebClient webClient = WebClient.builder()
                .baseUrl(godServiceBaseUrl)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
        WebClientAdapter adapter = WebClientAdapter.create(webClient);
        return HttpServiceProxyFactory.builderFor(adapter)
                .build();
    }

    @Bean
    GodQueryControllerHttpInterface godQueryControllerHttpInterface(HttpServiceProxyFactory factory) {
        return factory.createClient(GodQueryControllerHttpInterface.class);
    }
}
