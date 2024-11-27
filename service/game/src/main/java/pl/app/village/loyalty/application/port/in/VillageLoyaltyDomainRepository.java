package pl.app.village.loyalty.application.port.in;

import org.bson.types.ObjectId;
import pl.app.village.loyalty.application.domain.VillageLoyalty;
import pl.app.village.village.application.domain.Village;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface VillageLoyaltyDomainRepository {
    Mono<VillageLoyalty> fetchById(ObjectId villageId);

    Flux<VillageLoyalty> fetchVillagesWithoutMaxLoyalty();
}
