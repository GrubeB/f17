package pl.app.building.builder.application.port.in;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import pl.app.building.builder.application.domain.Builder;
import pl.app.building.builder.application.domain.BuilderEvent;
import pl.app.building.building.application.domain.BuildingType;
import pl.app.building.building.application.port.in.BuildingLevelDomainRepository;
import pl.app.building.village_infrastructure.application.port.in.VillageInfrastructureDomainRepository;
import pl.app.config.KafkaTopicConfigurationProperties;
import pl.app.resource.resource.application.domain.Resource;
import pl.app.resource.village_resource.application.port.in.VillageResourceCommand;
import pl.app.resource.village_resource.application.port.in.VillageResourceService;
import pl.app.village.village.application.port.in.VillageCommand;
import pl.app.village.village.application.port.in.VillageService;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BuilderServiceImplTest {

    @Autowired
    private BuilderServiceImpl service;
    @Autowired
    private VillageService villageService;
    @SpyBean
    private ReactiveMongoTemplate mongoTemplate;
    @SpyBean
    private KafkaTemplate<ObjectId, Object> kafkaTemplate;
    @SpyBean
    private KafkaTopicConfigurationProperties topicNames;

    @SpyBean
    private BuilderDomainRepository builderDomainRepository;
    @SpyBean
    private VillageInfrastructureDomainRepository villageInfrastructureDomainRepository;
    @SpyBean
    private BuildingLevelDomainRepository buildingLevelDomainRepository;
    @SpyBean
    private VillageResourceService villageResourceService;

    @Test
    void create_shouldCreateBuilder_whenCommandIsValid() {
        var villageId = ObjectId.get();
        var command = new BuilderCommand.CreateBuilderCommand(villageId);

        StepVerifier.create(service.crate(command))
                .assertNext(next -> {
                    assertThat(next).isNotNull();
                }).verifyComplete();
        verify(mongoTemplate, times(1)).insert(any(Builder.class));
        verify(kafkaTemplate, times(1))
                .send(eq(topicNames.getBuilderCreated().getName()), any(), any(BuilderEvent.BuilderCreatedEvent.class));
    }

    @Test
    void create_shouldThrow_whenThereIsBuilderForGivenVillage() {
        var villageId = ObjectId.get();
        var command = new BuilderCommand.CreateBuilderCommand(villageId);
        service.crate(command).block();
        StepVerifier.create(service.crate(command))
                .verifyError();
    }

    @Test
    void add_shouldAddConstruct_whenThereIsEnoughResources() {
        var village = villageService.crate(new VillageCommand.CreatePlayerVillageCommand(ObjectId.get())).block();
        villageResourceService.add(new VillageResourceCommand.AddResourceCommand(village.getId(), Resource.of(100_000))).block();
        Mockito.reset(mongoTemplate, kafkaTemplate);

        var command = new BuilderCommand.AddBuildingToConstructCommand(village.getId(), BuildingType.HEADQUARTERS);
        StepVerifier.create(service.add(command))
                .assertNext(next -> {
                    assertThat(next).isNotNull();
                }).verifyComplete();
        verify(mongoTemplate, times(1)).save(any(Builder.class));
        verify(kafkaTemplate, times(1))
                .send(eq(topicNames.getConstructAdded().getName()), any(), any(BuilderEvent.ConstructAddedEvent.class));
    }

    @Test
    void remove_shouldRemoveConstruct_whenCommandIsValid() {
        var village = villageService.crate(new VillageCommand.CreatePlayerVillageCommand(ObjectId.get())).block();
        villageResourceService.add(new VillageResourceCommand.AddResourceCommand(village.getId(), Resource.of(100_000))).block();
        service.add(new BuilderCommand.AddBuildingToConstructCommand(village.getId(), BuildingType.HEADQUARTERS)).block();
        Mockito.reset(mongoTemplate, kafkaTemplate, villageResourceService);

        var command = new BuilderCommand.RemoveBuildingToConstructCommand(village.getId(), BuildingType.HEADQUARTERS);
        StepVerifier.create(service.remove(command))
                .assertNext(next -> {
                    assertThat(next).isNotNull();
                }).verifyComplete();
        verify(mongoTemplate, times(1)).save(any(Builder.class));
        verify(kafkaTemplate, times(1))
                .send(eq(topicNames.getConstructRemoved().getName()), any(), any(BuilderEvent.ConstructRemovedEvent.class));
        verify(villageResourceService, times(1)).add(any());
    }
}