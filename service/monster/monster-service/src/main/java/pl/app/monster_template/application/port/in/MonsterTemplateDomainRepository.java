package pl.app.monster_template.application.port.in;

import org.bson.types.ObjectId;
import pl.app.monster_template.application.domain.MonsterTemplate;
import reactor.core.publisher.Mono;

public interface MonsterTemplateDomainRepository {
    Mono<MonsterTemplate> fetchById(ObjectId id);
}
