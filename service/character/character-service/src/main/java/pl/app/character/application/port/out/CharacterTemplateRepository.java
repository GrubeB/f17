package pl.app.character.application.port.out;

import org.bson.types.ObjectId;
import pl.app.character_template.application.domain.CharacterTemplate;
import reactor.core.publisher.Mono;

public interface CharacterTemplateRepository {
    Mono<CharacterTemplate> fetchById(ObjectId id);
    Mono<CharacterTemplate> fetchRandomTemplate();
}
