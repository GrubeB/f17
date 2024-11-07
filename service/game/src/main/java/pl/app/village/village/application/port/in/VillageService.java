package pl.app.village.village.application.port.in;

import pl.app.village.village.application.domain.Village;
import reactor.core.publisher.Mono;

public interface VillageService {
    Mono<Village> cratePlayerVillage(VillageCommand.CreatePlayerVillageCommand command);

    Mono<Village> crateBarbarianVillage(VillageCommand.CreateBarbarianVillageCommand command);
    Mono<Village> conquerVillage(VillageCommand.ConquerVillageCommand command);
    Mono<Village> subtractLoyalty(VillageCommand.SubtractLoyaltyCommand command);
    Mono<Village> refreshLoyalty(VillageCommand.RefreshLoyaltyCommand command);
}
