package pl.app.god_family.application.port.in;

import god_family.application.domain.GodFamily;
import pl.app.character.application.domain.Character;
import reactor.core.publisher.Mono;

public interface GodFamilyService {
    Mono<GodFamily> create(GodFamilyCommand.CreateGodFamilyCommand command);
    Mono<GodFamily> add(GodFamilyCommand.AddCharacterToGodFamilyCommand command);
    Mono<GodFamily> remove(GodFamilyCommand.RemoveCharacterFromGodFamilyCommand command);
}
