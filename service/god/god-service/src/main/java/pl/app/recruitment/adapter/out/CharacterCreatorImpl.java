package pl.app.recruitment.adapter.out;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import pl.app.character.application.port.in.CharacterCommand;
import pl.app.character.http.CharacterControllerHttpInterface;
import pl.app.character.query.dto.CharacterDto;
import pl.app.recruitment.application.port.out.CharacterCreator;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
class CharacterCreatorImpl implements CharacterCreator {
    private final CharacterControllerHttpInterface characterControllerHttpInterface;

    @Override
    public Mono<CharacterDto> createRandomCharacter() {
        var command = new CharacterCommand.CreateRandomCharacterCommand();
        return characterControllerHttpInterface.createRandomCharacter(command)
                .map(ResponseEntity::getBody);
    }
}
