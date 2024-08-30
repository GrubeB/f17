package pl.app.recruitment.application.port.out;

import pl.app.character.query.dto.CharacterDto;
import reactor.core.publisher.Mono;

public interface CharacterCreator {
    Mono<CharacterDto> createRandomCharacter();
}
