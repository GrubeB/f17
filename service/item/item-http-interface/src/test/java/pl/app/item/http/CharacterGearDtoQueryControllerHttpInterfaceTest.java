package pl.app.item.http;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import pl.app.common.shared.config.JacksonConfig;
import pl.app.equipment.dto.CharacterGearDto;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({ItemHttpInterfaceConfig.class, JacksonConfig.class})
@PropertySource("classpath:services.properties")
class CharacterGearDtoQueryControllerHttpInterfaceTest {
    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private CharacterGearDtoQueryControllerHttpInterface characterGearDtoQueryControllerHttpInterface;
    @Value("${app.service.item-service.base-url}")
    String itemServiceBaseUrl;

    @Test
    public void testFetchById() {
        ObjectId id = new ObjectId("66c801fe6d1174687e9c6d9c");
        webTestClient.get()
                .uri(itemServiceBaseUrl + "character-gears/" + id.toHexString())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Object.class)
                .value(characterGearDto -> {
                    assertNotNull(characterGearDto);
                });
    }

    @Test
    public void fetchAllByIds() {
        ObjectId id = new ObjectId("66c801fe6d1174687e9c6d9c");

        webTestClient.get()
                .uri(itemServiceBaseUrl + "character-gears?ids=" + id.toHexString())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<Page<CharacterGearDto>>() {
                })
                .value(dto -> {
                    assertNotNull(dto);
                });
    }
}