package pl.app.god_family.application.port.in;

import pl.app.god_family.application.domain.GodFamily;
import reactor.core.publisher.Mono;

public interface GodFamilyService {
    Mono<GodFamily> create(GodFamilyCommand.CreateGodFamilyCommand command);
    Mono<GodFamily> add(GodFamilyCommand.AddCharacterToGodFamilyCommand command);
    Mono<GodFamily> remove(GodFamilyCommand.RemoveCharacterFromGodFamilyCommand command);
}
