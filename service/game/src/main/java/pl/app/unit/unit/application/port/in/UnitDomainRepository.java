package pl.app.unit.unit.application.port.in;

import pl.app.unit.unit.application.domain.Unit;
import pl.app.unit.unit.application.domain.UnitType;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface UnitDomainRepository {
    Mono<Map<UnitType, Unit>> fetchAll();

    Mono<Unit> fetch(UnitType type);
}
