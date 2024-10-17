package pl.app.building.buildings.application.port.in;

import pl.app.building.buildings.application.domain.BuildingLevel;
import pl.app.building.buildings.application.domain.BuildingType;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface BuildingLevelDomainRepository {
    Mono<Map<Integer, ? extends BuildingLevel>> fetchAll(BuildingType type);

    Mono<? extends BuildingLevel> fetch(BuildingType type, Integer level);
}
