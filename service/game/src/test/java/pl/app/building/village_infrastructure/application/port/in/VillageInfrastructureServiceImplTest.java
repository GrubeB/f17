package pl.app.building.village_infrastructure.application.port.in;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import pl.app.building.building.application.port.in.BuildingLevelDomainRepository;
import pl.app.config.KafkaTopicConfigurationProperties;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class VillageInfrastructureServiceImplTest {
    @Autowired
    private VillageInfrastructureServiceImpl service;
    @SpyBean
    private ReactiveMongoTemplate mongoTemplate;
    @SpyBean
    private KafkaTemplate<ObjectId, Object> kafkaTemplate;
    @SpyBean
    private KafkaTopicConfigurationProperties topicNames;
    @SpyBean
    private VillageInfrastructureDomainRepository villageInfrastructureDomainRepository;
    @SpyBean
    private BuildingLevelDomainRepository buildingLevelDomainRepository;

    @Test
    void crate() {
        var villageId = ObjectId.get();
        var command = new VillageInfrastructureCommand.CreateVillageInfrastructureCommand(villageId);

        StepVerifier.create(service.crate(command))
                .assertNext(next -> {
                    assertThat(next).isNotNull();
                }).verifyComplete();
    }
}