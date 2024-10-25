package pl.app.map.village_position.application.port.in;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import pl.app.common.shared.test.AbstractIntegrationTest;
import pl.app.config.KafkaTopicConfigurationProperties;
import pl.app.map.map.application.port.in.MapDomainRepository;
import pl.app.map.village_position.application.domain.VillagePositionProvider;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class VillagePositionServiceImplTest extends AbstractIntegrationTest {

    @Autowired
    private VillagePositionServiceImpl service;
    @SpyBean
    private ReactiveMongoTemplate mongoTemplate;
    @SpyBean
    private KafkaTemplate<ObjectId, Object> kafkaTemplate;
    @SpyBean
    private KafkaTopicConfigurationProperties topicNames;
    @SpyBean
    private MapDomainRepository mapDomainRepository;
    @SpyBean
    private VillagePositionProvider villagePositionProvider;

    @Test
    void crate() {
        final var villageId = ObjectId.get();
        final var command = new VillagePositionCommand.CreateVillagePositionCommand(villageId);

        StepVerifier.create(service.crate(command))
                .expectNextCount(1)
                .verifyComplete();
    }
}