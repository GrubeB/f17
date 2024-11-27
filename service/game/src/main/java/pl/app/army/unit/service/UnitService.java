package pl.app.army.unit.service;

import pl.app.army.unit.model.Unit;
import pl.app.army.unit.model.UnitType;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface UnitService {
    Mono<Map<UnitType, Unit>> fetchAll();

    Mono<Unit> fetch(UnitType type);
}
