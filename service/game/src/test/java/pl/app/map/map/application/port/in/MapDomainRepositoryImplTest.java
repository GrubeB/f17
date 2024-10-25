package pl.app.map.map.application.port.in;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import pl.app.common.shared.test.AbstractIntegrationTest;
import pl.app.map.village_position.application.port.in.VillagePositionDomainRepository;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MapDomainRepositoryImplTest extends AbstractIntegrationTest {
    @Autowired
    private MapDomainRepositoryImpl service;
    @SpyBean
    private ReactiveMongoTemplate mongoTemplate;
    @SpyBean
    private VillagePositionDomainRepository villagePositionDomainRepository;

    @Test
    void fetch() {
        StepVerifier.create(service.fetch())
                .assertNext(next -> {
                    assertThat(next).isNotNull();
                })
                .verifyComplete();
    }
}