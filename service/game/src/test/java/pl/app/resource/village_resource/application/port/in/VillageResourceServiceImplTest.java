package pl.app.resource.village_resource.application.port.in;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import pl.app.building.building.application.domain.Buildings;
import pl.app.building.building.application.domain.building.ClayPitBuilding;
import pl.app.building.building.application.domain.building.IronMineBuilding;
import pl.app.building.building.application.domain.building.TimberCampBuilding;
import pl.app.building.building.application.domain.building.WarehouseBuilding;
import pl.app.building.village_infrastructure.query.VillageInfrastructureDtoQueryService;
import pl.app.building.village_infrastructure.query.dto.VillageInfrastructureDto;
import pl.app.common.shared.test.AbstractIntegrationTest;
import pl.app.config.KafkaTopicConfigurationProperties;
import pl.app.resource.resource.application.domain.Resource;
import pl.app.resource.village_resource.application.domain.VillageResource;
import pl.app.resource.village_resource.application.domain.VillageResourceEvent;
import pl.app.village.village.application.domain.Village;
import pl.app.village.village.application.port.in.VillageCommand;
import pl.app.village.village.application.port.in.VillageService;
import pl.app.village.village_effect.query.VillageEffectDtoQueryService;
import pl.app.village.village_effect.query.dto.VillageEffectDto;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class VillageResourceServiceImplTest {

    @Autowired
    private VillageResourceServiceImpl service;
    @Autowired
    private VillageService villageService;

    @SpyBean
    private ReactiveMongoTemplate mongoTemplate;
    @SpyBean
    private KafkaTemplate<ObjectId, Object> kafkaTemplate;
    @SpyBean
    private KafkaTopicConfigurationProperties topicNames;

    @Test
    void crate() {
        var villageId = ObjectId.get();

        StepVerifier.create(service.crate(new VillageResourceCommand.CreateVillageResourceCommand(villageId)))
                .assertNext(next -> {
                    assertThat(next).isNotNull();
                }).verifyComplete();
        verify(mongoTemplate, times(1)).insert(any(VillageResource.class));
        verify(kafkaTemplate, times(1))
                .send(eq(topicNames.getVillageResourceCreated().getName()), any(), any(VillageResourceEvent.VillageResourceCreatedEvent.class));
    }

    @Test
    void add() {
        Village village = villageService.crate(new VillageCommand.CreatePlayerVillageCommand(ObjectId.get())).block();
        System.out.println("/n/n/n/n/n/n/n/n/n");
        StepVerifier.create(service.add(new VillageResourceCommand.AddResourceCommand(village.getId(), Resource.of(100))))
                .assertNext(next -> {
                    assertThat(next).isNotNull();
                }).verifyComplete();
        verify(mongoTemplate, times(1)).save(any(VillageResource.class));
        verify(kafkaTemplate, times(1))
                .send(eq(topicNames.getResourceAdded().getName()), any(), any(VillageResourceEvent.ResourceAddedEvent.class));
    }
    @Test
    void refresh() throws InterruptedException {
        Village village = villageService.crate(new VillageCommand.CreatePlayerVillageCommand(ObjectId.get())).block();

        Thread.sleep(1_000);
        StepVerifier.create(service.refresh(new VillageResourceCommand.RefreshResourceCommand(village.getId())))
                .assertNext(next -> {
                    assertThat(next).isNotNull();
                }).verifyComplete();

        verify(mongoTemplate, times(1)).save(any(VillageResource.class));
        verify(kafkaTemplate, times(1))
                .send(eq(topicNames.getResourceAdded().getName()), any(), any(VillageResourceEvent.ResourceAddedEvent.class));
    }
}