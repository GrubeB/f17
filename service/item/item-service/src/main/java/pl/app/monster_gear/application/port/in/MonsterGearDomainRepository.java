package pl.app.monster_gear.application.port.in;

import org.bson.types.ObjectId;
import pl.app.monster_gear.application.domain.MonsterGear;
import reactor.core.publisher.Mono;

public interface MonsterGearDomainRepository {
    Mono<MonsterGear> fetchByMonsterId(ObjectId monsterId);
}
