package pl.app.map.village_position.application.port.in;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import pl.app.common.shared.test.AbstractIntegrationTest;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class VillagePositionDomainRepositoryImplTest extends AbstractIntegrationTest {

    @Autowired
    private VillagePositionDomainRepositoryImpl service;

    @SpyBean
    private ReactiveMongoTemplate mongoTemplate;

    @Test
    void fetchAll() {
        StepVerifier.create(service.fetchAll())
                .expectComplete();
    }
}