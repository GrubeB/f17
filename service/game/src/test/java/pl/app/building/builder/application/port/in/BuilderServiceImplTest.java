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
import pl.app.building.building.model.BuildingType;
import pl.app.building.building.service.BuildingLevelService;
import pl.app.building.village_infrastructure.application.port.in.VillageInfrastructureDomainRepository;
import pl.app.common.shared.test.AbstractIntegrationTest;
import pl.app.config.KafkaTopicConfigurationProperties;
import pl.app.resource.share.Resource;
import pl.app.resource.village_resource.application.port.in.VillageResourceCommand;
import pl.app.resource.village_resource.application.port.in.VillageResourceService;
import pl.app.village.village.application.port.in.VillageCommand;
import pl.app.village.village.application.port.in.VillageService;
import reactor.test.StepVerifier;

import java.util.Iterator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BuilderServiceImplTest extends AbstractIntegrationTest {

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
    private BuildingLevelService buildingLevelDomainRepository;
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
        var village = villageService.cratePlayerVillage(new VillageCommand.CreatePlayerVillageCommand(ObjectId.get())).block();
        villageResourceService.add(new VillageResourceCommand.AddResourceCommand(village.getId(), Resource.of(100_000))).block();
        Mockito.reset(mongoTemplate, kafkaTemplate);

        StepVerifier.create(service.add(new BuilderCommand.AddBuildingToConstructCommand(village.getId(), BuildingType.HEADQUARTERS)))
                .assertNext(next -> {
                    assertThat(next).isNotNull();
                }).verifyComplete();
        verify(mongoTemplate, times(1)).save(any(Builder.class));
        verify(kafkaTemplate, times(1))
                .send(eq(topicNames.getConstructAdded().getName()), any(), any(BuilderEvent.ConstructAddedEvent.class));
    }
    @Test
    void start_shouldStart_whenThereIsConstructionIsQueue() {
        var village = villageService.cratePlayerVillage(new VillageCommand.CreatePlayerVillageCommand(ObjectId.get())).block();
        villageResourceService.add(new VillageResourceCommand.AddResourceCommand(village.getId(), Resource.of(100_000))).block();
        service.add(new BuilderCommand.AddBuildingToConstructCommand(village.getId(), BuildingType.HEADQUARTERS)).block();
        Mockito.reset(mongoTemplate, kafkaTemplate);

        StepVerifier.create(service.start(new BuilderCommand.StartBuildingToConstructCommand(village.getId())))
                .assertNext(next -> {
                    assertThat(next).isNotNull();
                }).verifyComplete();
        verify(mongoTemplate, times(1)).save(any(Builder.class));
        verify(kafkaTemplate, times(1))
                .send(eq(topicNames.getConstructStarted().getName()), any(), any(BuilderEvent.ConstructStartedEvent.class));
    }
    @Test
    void finish_shouldFinish_whenIsAfterToDate() throws InterruptedException {
        var village = villageService.cratePlayerVillage(new VillageCommand.CreatePlayerVillageCommand(ObjectId.get())).block();
        villageResourceService.add(new VillageResourceCommand.AddResourceCommand(village.getId(), Resource.of(100_000))).block();
        service.add(new BuilderCommand.AddBuildingToConstructCommand(village.getId(), BuildingType.CLAY_PIT)).block();
        service.start(new BuilderCommand.StartBuildingToConstructCommand(village.getId())).block();
        Mockito.reset(mongoTemplate, kafkaTemplate);
        Thread.sleep(6_000);
        StepVerifier.create(service.finish(new BuilderCommand.FinishBuildingConstructCommand(village.getId())))
                .assertNext(next -> {
                    assertThat(next).isNotNull();
                    assertThat(next.getConstructs().size()).isEqualTo(0);
                }).verifyComplete();
        verify(mongoTemplate, times(1)).save(any(Builder.class));
        verify(kafkaTemplate, times(1))
                .send(any(), any(), any(BuilderEvent.ConstructFinishedEvent.class));
    }

    @Test
    void reject_shouldRejectAllConstructions() {
        var village = villageService.cratePlayerVillage(new VillageCommand.CreatePlayerVillageCommand(ObjectId.get())).block();
        villageResourceService.add(new VillageResourceCommand.AddResourceCommand(village.getId(), Resource.of(100_000))).block();
        service.add(new BuilderCommand.AddBuildingToConstructCommand(village.getId(), BuildingType.IRON_MINE)).block();
        service.add(new BuilderCommand.AddBuildingToConstructCommand(village.getId(), BuildingType.IRON_MINE)).block();
        Mockito.reset(mongoTemplate, kafkaTemplate);

        StepVerifier.create(service.reject(new BuilderCommand.RejectBuildingConstructCommand(village.getId(),BuildingType.IRON_MINE)))
                .assertNext(next -> {
                    assertThat(next).isNotNull();
                }).verifyComplete();
        verify(mongoTemplate, times(1)).save(any(Builder.class));
        verify(kafkaTemplate, times(2))
                .send(eq(topicNames.getConstructRejected().getName()), any(), any(BuilderEvent.ConstructRejectedEvent.class));
    }

    @Test
    void cancel_shouldCancelConstruct_whenCommandIsValid() {
        var village = villageService.cratePlayerVillage(new VillageCommand.CreatePlayerVillageCommand(ObjectId.get())).block();
        villageResourceService.add(new VillageResourceCommand.AddResourceCommand(village.getId(), Resource.of(100_000))).block();
        service.add(new BuilderCommand.AddBuildingToConstructCommand(village.getId(), BuildingType.IRON_MINE)).block();
        service.add(new BuilderCommand.AddBuildingToConstructCommand(village.getId(), BuildingType.IRON_MINE)).block();
        service.add(new BuilderCommand.AddBuildingToConstructCommand(village.getId(), BuildingType.IRON_MINE)).block();
        service.add(new BuilderCommand.AddBuildingToConstructCommand(village.getId(), BuildingType.IRON_MINE)).block();
        Mockito.reset(mongoTemplate, kafkaTemplate, villageResourceService);

        StepVerifier.create(service.cancel(new BuilderCommand.CancelBuildingConstructCommand(village.getId(), BuildingType.IRON_MINE,2)))
                .assertNext(next -> {
                    assertThat(next).isNotNull();
                }).verifyComplete();

        verify(mongoTemplate, times(1)).save(any(Builder.class));
        verify(kafkaTemplate, times(3))
                .send(eq(topicNames.getConstructCanceled().getName()), any(), any(BuilderEvent.ConstructCanceledEvent.class));
    }
    @Test
    void cancel_shouldCancelConstructAndRescheduleRemainderConstructions() {
        var village = villageService.cratePlayerVillage(new VillageCommand.CreatePlayerVillageCommand(ObjectId.get())).block();
        villageResourceService.add(new VillageResourceCommand.AddResourceCommand(village.getId(), Resource.of(100_000))).block();
        service.add(new BuilderCommand.AddBuildingToConstructCommand(village.getId(), BuildingType.IRON_MINE)).block();
        service.add(new BuilderCommand.AddBuildingToConstructCommand(village.getId(), BuildingType.TIMBER_CAMP)).block();
        service.add(new BuilderCommand.AddBuildingToConstructCommand(village.getId(), BuildingType.IRON_MINE)).block();
        service.add(new BuilderCommand.AddBuildingToConstructCommand(village.getId(), BuildingType.TIMBER_CAMP)).block();
        service.add(new BuilderCommand.AddBuildingToConstructCommand(village.getId(), BuildingType.IRON_MINE)).block();
        Mockito.reset(mongoTemplate, kafkaTemplate, villageResourceService);

        StepVerifier.create(service.cancel(new BuilderCommand.CancelBuildingConstructCommand(village.getId(), BuildingType.IRON_MINE,1)))
                .assertNext(next -> {
                    assertThat(next).isNotNull();
                    assertThat(next.getConstructs().size()).isEqualTo(2);
                    Iterator<Builder.Construct> iterator = next.getConstructs().iterator();
                    assertThat(iterator.next().getTo()).isEqualTo(iterator.next().getFrom());
                }).verifyComplete();

        verify(mongoTemplate, times(1)).save(any(Builder.class));
        verify(kafkaTemplate, times(3))
                .send(eq(topicNames.getConstructCanceled().getName()), any(), any(BuilderEvent.ConstructCanceledEvent.class));
    }
}