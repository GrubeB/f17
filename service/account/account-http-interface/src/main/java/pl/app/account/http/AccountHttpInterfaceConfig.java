package pl.app.account.http;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

public class AccountHttpInterfaceConfig {

    @Bean
    HttpServiceProxyFactory itemServiceHttpServiceProxyFactory(
            @Value("${app.service.account-service.base-url}") String accountServiceBaseUrl
    ) {
        WebClient webClient = WebClient.builder().baseUrl(accountServiceBaseUrl).build();
        WebClientAdapter adapter = WebClientAdapter.create(webClient);
        return HttpServiceProxyFactory.builderFor(adapter).build();
    }

    @Bean
    AccountQueryControllerHttpInterface accountQueryControllerHttpInterface(HttpServiceProxyFactory factory) {
        return factory.createClient(AccountQueryControllerHttpInterface.class);
    }
}
