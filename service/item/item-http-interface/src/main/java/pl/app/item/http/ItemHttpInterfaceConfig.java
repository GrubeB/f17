package pl.app.item.http;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;


public class ItemHttpInterfaceConfig {

    @Value("${app.service.item-service.base-url}")
    private String itemServiceBaseUrl;

    HttpServiceProxyFactory factory() {
        WebClient webClient = WebClient.builder()
                .baseUrl(itemServiceBaseUrl)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
        WebClientAdapter adapter = WebClientAdapter.create(webClient);
        return HttpServiceProxyFactory.builderFor(adapter)
                .build();
    }

    @Bean
    OutfitQueryControllerHttpInterface outfitQueryControllerHttpInterface() {
        return factory().createClient(OutfitQueryControllerHttpInterface.class);
    }

    @Bean
    OutfitTemplateQueryControllerHttpInterface outfitTemplateQueryControllerHttpInterface() {
        return factory().createClient(OutfitTemplateQueryControllerHttpInterface.class);
    }

    @Bean
    WeaponQueryControllerHttpInterface weaponQueryControllerHttpInterface() {
        return factory().createClient(WeaponQueryControllerHttpInterface.class);
    }

    @Bean
    WeaponTemplateQueryControllerHttpInterface weaponTemplateQueryControllerHttpInterface() {
        return factory().createClient(WeaponTemplateQueryControllerHttpInterface.class);
    }

    @Bean
    GodEquipmentQueryControllerHttpInterface godEquipmentQueryControllerHttpInterface() {
        return factory().createClient(GodEquipmentQueryControllerHttpInterface.class);
    }

    @Bean
    CharacterGearDtoQueryControllerHttpInterface characterGearDtoQueryControllerHttpInterface() {
        return factory().createClient(CharacterGearDtoQueryControllerHttpInterface.class);
    }
}
