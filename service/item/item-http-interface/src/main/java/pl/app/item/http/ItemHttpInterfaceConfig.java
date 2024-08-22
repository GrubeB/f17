package pl.app.item.http;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

public class ItemHttpInterfaceConfig {

    @Bean
    HttpServiceProxyFactory itemServiceHttpServiceProxyFactory(
            @Value("${app.service.item-service.base-url}") String itemServiceBaseUrl
    ) {
        WebClient webClient = WebClient.builder().baseUrl(itemServiceBaseUrl).build();
        WebClientAdapter adapter = WebClientAdapter.create(webClient);
        return HttpServiceProxyFactory.builderFor(adapter).build();
    }

    @Bean
    OutfitQueryControllerHttpInterface outfitQueryControllerHttpInterface(HttpServiceProxyFactory factory) {
        return factory.createClient(OutfitQueryControllerHttpInterface.class);
    }

    @Bean
    OutfitTemplateQueryControllerHttpInterface outfitTemplateQueryControllerHttpInterface(HttpServiceProxyFactory factory) {
        return factory.createClient(OutfitTemplateQueryControllerHttpInterface.class);
    }

    @Bean
    WeaponQueryControllerHttpInterface weaponQueryControllerHttpInterface(HttpServiceProxyFactory factory) {
        return factory.createClient(WeaponQueryControllerHttpInterface.class);
    }

    @Bean
    WeaponTemplateQueryControllerHttpInterface weaponTemplateQueryControllerHttpInterface(HttpServiceProxyFactory factory) {
        return factory.createClient(WeaponTemplateQueryControllerHttpInterface.class);
    }

    @Bean
    AccountEquipmentQueryControllerHttpInterface accountEquipmentQueryControllerHttpInterface(HttpServiceProxyFactory factory) {
        return factory.createClient(AccountEquipmentQueryControllerHttpInterface.class);
    }
}
