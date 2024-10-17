package pl.app.village.village.application.port.in;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import pl.app.config.KafkaTopicConfigurationProperties;
import pl.app.map.village_position.application.port.in.VillagePositionService;
import pl.app.resource.village_resource.application.port.in.VillageResourceService;
import pl.app.village.village.application.domain.Village;
import pl.app.village.village.application.domain.VillageEvent;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class VillageInfrastructureServiceImplTest {
    @Autowired
    private VillageServiceImpl service;
    @SpyBean
    private ReactiveMongoTemplate mongoTemplate;
    @SpyBean
    private KafkaTemplate<ObjectId, Object> kafkaTemplate;
    @SpyBean
    private KafkaTopicConfigurationProperties topicNames;
    @SpyBean
    private VillageResourceService villageResourceService;
    @SpyBean
    private VillagePositionService villagePositionService;

    @Test
    void crate_shouldCreateVillage_whenCommandIsValid() {
        var playerId = ObjectId.get();
        var command = new VillageCommand.CreatePlayerVillageCommand(playerId);

        StepVerifier.create(service.crate(command))
                .assertNext(next -> {
                    assertThat(next).isNotNull();
                }).verifyComplete();

        verify(mongoTemplate, times(1)).insert(any(Village.class));
        verify(kafkaTemplate, times(1))
                .send(eq(topicNames.getVillageCreated().getName()), any(), any(VillageEvent.VillageCreatedEvent.class));

    }
}