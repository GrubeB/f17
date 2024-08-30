package pl.app.account.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.util.Set;
import java.util.stream.Collectors;

public class GodHttpInterfaceConfig {

    @Value("${app.service.god-service.base-url}")
    private String serviceBaseUrl;
    private final ObjectMapper objectMapper;

    public GodHttpInterfaceConfig(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    HttpServiceProxyFactory factory() {
        ExchangeStrategies jacksonStrategy = ExchangeStrategies.builder()
                .codecs(config -> {
                    config.defaultCodecs().jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper, MediaType.APPLICATION_JSON));
                    config.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper, MediaType.APPLICATION_JSON));
                }).build();
        WebClient webClient = WebClient.builder()
                .exchangeStrategies(jacksonStrategy)
                .baseUrl(serviceBaseUrl)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
        WebClientAdapter adapter = WebClientAdapter.create(webClient);
        return HttpServiceProxyFactory.builderFor(adapter)
                .build();
    }

    @Bean
    GodControllerHttpInterface godControllerHttpInterface() {
        return factory().createClient(GodControllerHttpInterface.class);
    }
    @Bean
    GodQueryControllerHttpInterface godQueryControllerHttpInterface() {
        return factory().createClient(GodQueryControllerHttpInterface.class);
    }

    @Bean
    GodMoneyControllerHttpInterface godMoneyControllerHttpInterface() {
        return factory().createClient(GodMoneyControllerHttpInterface.class);
    }
}
