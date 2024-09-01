package pl.app.character_template.application.port.in;

import pl.app.character_template.application.domain.CharacterTemplate;
import pl.app.character_template.in.CharacterCommand;
import reactor.core.publisher.Mono;


public interface CharacterTemplateService {
    Mono<CharacterTemplate> create(CharacterCommand.CreateCharacterTemplateCommand command);

    Mono<CharacterTemplate> update(CharacterCommand.UpdateCharacterTemplateCommand command);

    Mono<CharacterTemplate> delete(CharacterCommand.DeleteCharacterTemplateCommand command);
}
