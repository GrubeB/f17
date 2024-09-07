package pl.app.monster.application.port.in;

import org.bson.types.ObjectId;
import pl.app.monster.application.domain.Monster;
import reactor.core.publisher.Mono;

import java.util.Set;

public interface MonsterDomainRepository {
    Mono<Monster> fetchById(ObjectId id);
    Mono<Set<Monster>> fetchAllById(Set<ObjectId> ids);
}
