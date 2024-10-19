package pl.app.building.builder.application.port.in;

import org.bson.types.ObjectId;
import pl.app.building.builder.application.domain.Builder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

public interface BuilderDomainRepository {
    Mono<Builder> fetchByVillageId(ObjectId villageId);

    Flux<Builder> fetchBuildersWithConstructEnding(Duration withinTime);
}
