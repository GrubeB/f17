package pl.app.building.building.service;

import pl.app.building.building.model.BuildingLevel;
import pl.app.building.building.model.BuildingType;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface BuildingLevelService {
    Mono<Map<Integer, ? extends BuildingLevel>> fetchAll(BuildingType type);

    Mono<? extends BuildingLevel> fetch(BuildingType type, Integer level);
}
