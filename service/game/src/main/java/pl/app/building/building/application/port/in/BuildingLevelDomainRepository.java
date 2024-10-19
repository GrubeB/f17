package pl.app.building.building.application.port.in;

import pl.app.building.building.application.domain.BuildingLevel;
import pl.app.building.building.application.domain.BuildingType;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface BuildingLevelDomainRepository {
    Mono<Map<Integer, ? extends BuildingLevel>> fetchAll(BuildingType type);

    Mono<? extends BuildingLevel> fetch(BuildingType type, Integer level);
}
