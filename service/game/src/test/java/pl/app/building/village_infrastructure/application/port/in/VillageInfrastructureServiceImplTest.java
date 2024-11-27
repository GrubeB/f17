package pl.app.building.village_infrastructure.application.port.in;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.app.building.building.model.BuildingType;
import pl.app.common.shared.test.AbstractIntegrationTest;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class VillageInfrastructureServiceImplTest extends AbstractIntegrationTest {
    @Autowired
    private VillageInfrastructureServiceImpl service;

    @Test
    void crate() {
        var villageId = ObjectId.get();
        StepVerifier.create(service.crate(new VillageInfrastructureCommand.CreateVillageInfrastructureCommand(villageId)))
                .assertNext(next -> {
                    assertThat(next).isNotNull();
                }).verifyComplete();
    }

    @Test
    void levelUp_shouldLevelUp_whenBuildingLevelIsLowerThanToLevelParameter() {
        var villageId = ObjectId.get();
        service.crate(new VillageInfrastructureCommand.CreateVillageInfrastructureCommand(villageId)).block();
        StepVerifier.create(service.levelUp(new VillageInfrastructureCommand.LevelUpVillageInfrastructureBuildingCommand(
                        villageId, BuildingType.HEADQUARTERS, 10
                )))
                .assertNext(next -> {
                    assertThat(next).isNotNull();
                }).verifyComplete();
    }
    @Test
    void levelUp_shouldThrow_whenBuildingLevelIsHigherThanToLevelParameter() {
        var villageId = ObjectId.get();
        service.crate(new VillageInfrastructureCommand.CreateVillageInfrastructureCommand(villageId)).block();
        service.levelUp(new VillageInfrastructureCommand.LevelUpVillageInfrastructureBuildingCommand(
                        villageId, BuildingType.HEADQUARTERS, 10
                )).block();

        StepVerifier.create(service.levelUp(new VillageInfrastructureCommand.LevelUpVillageInfrastructureBuildingCommand(
                        villageId, BuildingType.HEADQUARTERS,2
                )))
                .verifyError();
    }
    @Test
    void levelDown_shouldLevelDown_whenBuildingLevelIsHigherThanToLevelParameter() {
        var villageId = ObjectId.get();
        service.crate(new VillageInfrastructureCommand.CreateVillageInfrastructureCommand(villageId)).block();
        service.levelUp(new VillageInfrastructureCommand.LevelUpVillageInfrastructureBuildingCommand(
                villageId, BuildingType.HEADQUARTERS, 10
        )).block();
        StepVerifier.create(service.levelDown(new VillageInfrastructureCommand.LevelDownVillageInfrastructureBuildingCommand(
                        villageId, BuildingType.HEADQUARTERS, 9
                )))
                .assertNext(next -> {
                    assertThat(next).isNotNull();
                }).verifyComplete();
    }
    @Test
    void levelDown_shouldThrow_whenBuildingLevelIsLowerThanToLevelParameter() {
        var villageId = ObjectId.get();
        service.crate(new VillageInfrastructureCommand.CreateVillageInfrastructureCommand(villageId)).block();
        service.levelUp(new VillageInfrastructureCommand.LevelUpVillageInfrastructureBuildingCommand(
                villageId, BuildingType.HEADQUARTERS, 10
        )).block();
        StepVerifier.create(service.levelDown(new VillageInfrastructureCommand.LevelDownVillageInfrastructureBuildingCommand(
                        villageId, BuildingType.HEADQUARTERS, 12
                )))
                .verifyError();
    }
}