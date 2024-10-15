package pl.app.map.village_position.application.port.in;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class VillagePositionDomainRepositoryImplTest {

    @Autowired
    private VillagePositionDomainRepositoryImpl service;

    @SpyBean
    private ReactiveMongoTemplate mongoTemplate;

    @Test
    void fetchAll() {
        StepVerifier.create(service.fetchAll())
                .verifyComplete();
    }
}