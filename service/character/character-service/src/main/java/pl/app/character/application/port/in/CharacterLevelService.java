package pl.app.character.application.port.in;

import pl.app.character.application.domain.Character;
import reactor.core.publisher.Mono;

public interface CharacterLevelService {
    Mono<Character> addExp(CharacterCommand.AddExpCommand command);
}
