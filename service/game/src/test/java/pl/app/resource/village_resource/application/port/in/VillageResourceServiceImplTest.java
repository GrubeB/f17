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
import pl.app.building.building.application.domain.building.ClayPitBuilding;
import pl.app.building.building.application.domain.building.IronMineBuilding;
import pl.app.building.building.application.domain.building.TimberCampBuilding;
import pl.app.building.building.application.domain.building.WarehouseBuilding;
import pl.app.building.village_infrastructure.query.VillageInfrastructureDtoQueryService;
import pl.app.building.village_infrastructure.query.dto.VillageInfrastructureDto;
import pl.app.config.KafkaTopicConfigurationProperties;
import pl.app.resource.resource.application.domain.Resource;
import pl.app.resource.village_resource.application.domain.VillageResource;
import pl.app.resource.village_resource.application.domain.VillageResourceEvent;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

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

    @SpyBean
    private ReactiveMongoTemplate mongoTemplate;
    @SpyBean
    private KafkaTemplate<ObjectId, Object> kafkaTemplate;
    @SpyBean
    private KafkaTopicConfigurationProperties topicNames;

    @SpyBean
    private VillageResourceDomainRepository villageResourceDomainRepository;
    @MockBean
    private VillageInfrastructureDtoQueryService villageInfrastructureDtoQueryService;


    @Test
    void crate() {
        var villageId = ObjectId.get();
        var command = new VillageResourceCommand.CreateVillageResourceCommand(villageId);

        StepVerifier.create(service.crate(command))
                .assertNext(next -> {
                    assertThat(next).isNotNull();
                }).verifyComplete();
        verify(mongoTemplate, times(1)).insert(any(VillageResource.class));
        verify(kafkaTemplate, times(1))
                .send(eq(topicNames.getVillageResourceCreated().getName()), any(), any(VillageResourceEvent.VillageResourceCreatedEvent.class));
    }

    @Test
    void add() {
        var villageId = ObjectId.get();
        service.crate(new VillageResourceCommand.CreateVillageResourceCommand(villageId)).block();
        Mockito.reset(mongoTemplate, kafkaTemplate);
        Mockito.when(villageInfrastructureDtoQueryService.fetchByVillageId(any())).thenReturn(getExampleVillageInfrastructureDto());
        StepVerifier.create(service.add(new VillageResourceCommand.AddResourceCommand(villageId, Resource.of(100))))
                .assertNext(next -> {
                    assertThat(next).isNotNull();
                }).verifyComplete();
        verify(mongoTemplate, times(1)).save(any(VillageResource.class));
        verify(kafkaTemplate, times(1))
                .send(eq(topicNames.getResourceAdded().getName()), any(), any(VillageResourceEvent.ResourceAddedEvent.class));
    }

    private Mono<VillageInfrastructureDto> getExampleVillageInfrastructureDto() {
        return Mono.just(VillageInfrastructureDto.builder()
                .warehouse(new WarehouseBuilding(0, 10_000))
                .timberCamp(new TimberCampBuilding(0, 3_600))
                .clayPit(new ClayPitBuilding(0, 3_600))
                .ironMine(new IronMineBuilding(0, 3_600))
                .build());
    }

    @Test
    void refresh() throws InterruptedException {
        VirtualTimeScheduler.getOrSet();
        var villageId = ObjectId.get();
        service.crate(new VillageResourceCommand.CreateVillageResourceCommand(villageId)).block();
        Mockito.reset(mongoTemplate, kafkaTemplate);
        Mockito.when(villageInfrastructureDtoQueryService.fetchByVillageId(any())).thenReturn(getExampleVillageInfrastructureDto());

        Thread.sleep(10_000);
        StepVerifier.create(service.refresh(new VillageResourceCommand.RefreshResourceCommand(villageId)))
                .assertNext(next -> {
                    assertThat(next).isNotNull();
                }).verifyComplete();

        verify(mongoTemplate, times(1)).save(any(VillageResource.class));
        verify(kafkaTemplate, times(1))
                .send(eq(topicNames.getResourceAdded().getName()), any(), any(VillageResourceEvent.ResourceAddedEvent.class));
    }
}