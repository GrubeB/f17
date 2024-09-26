package pl.app.family.application.port.in;

import pl.app.family.application.domain.Family;
import reactor.core.publisher.Mono;

public interface FamilyService {
    Mono<Family> create(FamilyCommand.CreateFamilyCommand command);
    Mono<Family> add(FamilyCommand.AddCharacterToFamilyCommand command);
    Mono<Family> remove(FamilyCommand.RemoveCharacterFromFamilyCommand command);
}
