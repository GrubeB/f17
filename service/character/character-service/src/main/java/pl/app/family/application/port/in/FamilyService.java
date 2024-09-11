package pl.app.family.application.port.in;

import pl.app.family.application.domain.Family;
import reactor.core.publisher.Mono;

public interface FamilyService {
    Mono<Family> create(FamilyCommand.CreateGodFamilyCommand command);
    Mono<Family> add(FamilyCommand.AddCharacterToGodFamilyCommand command);
    Mono<Family> remove(FamilyCommand.RemoveCharacterFromGodFamilyCommand command);
}
