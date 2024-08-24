package pl.app.god_equipment.application.port.in;

import pl.app.god_equipment.application.domain.CharacterGear;
import reactor.core.publisher.Mono;

public interface CharacterGearService {
    Mono<CharacterGear> crateCharacterGear(CharacterGearCommand.CreateCharacterGearCommand command);
    Mono<CharacterGear> removeCharacterGear(CharacterGearCommand.RemoveCharacterGearCommand command);
}
