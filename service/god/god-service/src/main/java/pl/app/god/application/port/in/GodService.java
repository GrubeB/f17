package pl.app.god.application.port.in;

import pl.app.god.application.domain.God;
import reactor.core.publisher.Mono;


public interface GodService {
    Mono<God> create(GodCommand.CreateGodCommand command);
}
