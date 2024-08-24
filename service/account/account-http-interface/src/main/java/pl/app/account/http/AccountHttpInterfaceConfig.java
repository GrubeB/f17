package pl.app.account.http;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

public class AccountHttpInterfaceConfig {

    @Value("${app.service.account-service.base-url}")
    private String accountServiceBaseUrl;

    HttpServiceProxyFactory factory() {
        WebClient webClient = WebClient.builder()
                .baseUrl(accountServiceBaseUrl)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
        WebClientAdapter adapter = WebClientAdapter.create(webClient);
        return HttpServiceProxyFactory.builderFor(adapter)
                .build();
    }

    @Bean
    AccountQueryControllerHttpInterface accountQueryControllerHttpInterface() {
        return factory().createClient(AccountQueryControllerHttpInterface.class);
    }
}
