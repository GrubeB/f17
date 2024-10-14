package pl.app.map.village_position.application.port.in;

import pl.app.map.village_position.application.domain.VillagePosition;
import reactor.core.publisher.Flux;

public interface VillagePositionDomainRepository {
    Flux<VillagePosition> fetchAll();
}
