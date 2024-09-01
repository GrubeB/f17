package pl.app.character_template.application.port.out;

import org.bson.types.ObjectId;
import pl.app.character_template.application.domain.CharacterTemplate;
import reactor.core.publisher.Mono;

public interface CharacterTemplateDomainRepository {
    Mono<CharacterTemplate> fetchById(ObjectId id);
}
