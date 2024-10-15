package pl.app.map.village_position.application.domain;

import pl.app.map.map.application.domain.Position;
import reactor.core.publisher.Mono;

public interface VillagePositionProvider {
    Mono<Position> getNewVillagePosition();
}
