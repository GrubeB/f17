package pl.app.map.village_position.application.port.in;

import pl.app.map.village_position.application.domain.VillagePosition;
import reactor.core.publisher.Mono;

public interface VillagePositionService {
    Mono<VillagePosition> crate(VillagePositionCommand.CreateVillagePositionCommand command);
}
