package pl.app.village.village_effect.application.port.in;

import org.bson.types.ObjectId;
import pl.app.attack.army_walk.domain.application.ArmyWalk;
import pl.app.village.village_effect.application.domain.VillageEffect;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

public interface VillageEffectDomainRepository {
    Mono<VillageEffect> fetchByVillageId(ObjectId villageId);

    Flux<VillageEffect> fetchVillageEffectWithEnding(Duration withinTime);
}
