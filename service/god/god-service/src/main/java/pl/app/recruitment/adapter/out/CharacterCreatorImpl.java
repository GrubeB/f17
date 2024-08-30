package pl.app.recruitment.adapter.out;

import com.github.javafaker.Faker;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import pl.app.character.application.port.in.CharacterCommand;
import pl.app.character.http.CharacterControllerHttpInterface;
import pl.app.character.query.dto.CharacterDto;
import pl.app.common.shared.model.CharacterProfession;
import pl.app.recruitment.application.port.out.CharacterCreator;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
class CharacterCreatorImpl implements CharacterCreator {
    private final CharacterControllerHttpInterface characterControllerHttpInterface;

    @Override
    public Mono<CharacterDto> createRandomCharacter() {
        Faker faker = Faker.instance();
        var command = new CharacterCommand.CreateCharacterCommand(faker.name().name(), CharacterProfession.WARRIOR);
        return characterControllerHttpInterface.createCharacter(command)
                .map(ResponseEntity::getBody);
    }
}
