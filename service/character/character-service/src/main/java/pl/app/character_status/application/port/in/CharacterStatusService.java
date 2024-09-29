package pl.app.character_status.application.port.in;

import pl.app.character_status.application.domain.CharacterStatus;
import reactor.core.publisher.Mono;

public interface CharacterStatusService {
    Mono<CharacterStatus> changeStatus(CharacterStatusCommand.ChangeCharacterStatusCommand command);
}
