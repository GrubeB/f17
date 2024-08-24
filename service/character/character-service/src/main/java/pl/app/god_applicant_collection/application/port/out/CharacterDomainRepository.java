package pl.app.god_applicant_collection.application.port.out;

import org.bson.types.ObjectId;
import pl.app.character.application.domain.Character;
import reactor.core.publisher.Mono;

public interface CharacterDomainRepository {
    Mono<Character> fetchById(ObjectId id);
}
