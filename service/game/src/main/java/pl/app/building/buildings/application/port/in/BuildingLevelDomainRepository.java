package pl.app.building.buildings.application.port.in;

import pl.app.building.buildings.application.domain.BuildingLevel;
import pl.app.building.buildings.application.domain.BuildingType;
import reactor.core.publisher.Mono;

import java.util.Set;

public interface BuildingLevelDomainRepository {
    Mono<Set<? extends BuildingLevel>> fetchAll(BuildingType type);

    Mono<? extends BuildingLevel> fetch(BuildingType type, Integer level);
}
