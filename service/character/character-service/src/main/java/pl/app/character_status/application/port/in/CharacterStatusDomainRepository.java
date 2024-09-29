package pl.app.character_status.application.port.in;

import org.bson.types.ObjectId;
import pl.app.character.application.domain.Character;
import pl.app.character_status.application.domain.CharacterStatus;
import reactor.core.publisher.Mono;

public interface CharacterStatusDomainRepository {
    Mono<CharacterStatus> fetchByCharacterId(ObjectId characterId);
}
