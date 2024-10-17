package pl.app.building.village_infrastructure.application.port.in;

import pl.app.building.village_infrastructure.application.domain.VillageInfrastructure;
import reactor.core.publisher.Mono;

public interface VillageInfrastructureService {
    Mono<VillageInfrastructure> crate(VillageInfrastructureCommand.CreateVillageInfrastructureCommand command);

    Mono<VillageInfrastructure> levelUp(VillageInfrastructureCommand.LevelUpVillageInfrastructureBuildingCommand command);

    Mono<VillageInfrastructure> levelDown(VillageInfrastructureCommand.LevelDownVillageInfrastructureBuildingCommand command);
}
