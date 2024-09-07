package pl.app.character.http;

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

public class MonsterHttpInterfaceConfig {
    @Value("${app.service.monster-service.base-url}")
    private String characterServiceBaseUrl;
    private ObjectMapper objectMapper;

    public MonsterHttpInterfaceConfig(ObjectMapper objectMapper) {
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
                .baseUrl(characterServiceBaseUrl)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
        WebClientAdapter adapter = WebClientAdapter.create(webClient);
        return HttpServiceProxyFactory.builderFor(adapter)
                .build();
    }

    @Bean
    MonsterQueryControllerHttpInterface monsterQueryControllerHttpInterface() {
        return factory().createClient(MonsterQueryControllerHttpInterface.class);
    }

    @Bean
    MonsterWithGearDtoQueryControllerHttpInterface monsterWithGearDtoQueryControllerHttpInterface() {
        return factory().createClient(MonsterWithGearDtoQueryControllerHttpInterface.class);
    }
}