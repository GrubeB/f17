package pl.app.unit.village_army.application.port.in;

import pl.app.unit.village_army.application.domain.VillageArmy;
import reactor.core.publisher.Mono;

public interface VillageArmyService {
    Mono<VillageArmy> crate(VillageArmyCommand.CreateVillageArmyCommand command);

    Mono<VillageArmy> add(VillageArmyCommand.AddUnitsCommand command);

    Mono<VillageArmy> subtract(VillageArmyCommand.SubtractUnitsCommand command);

    Mono<VillageArmy> block(VillageArmyCommand.BlockUnitsCommand command);

    Mono<VillageArmy> unblock(VillageArmyCommand.UnblockUnitsCommand command);
}
