package pl.app.god.application.port.in;

import pl.app.god.application.domain.God;
import reactor.core.publisher.Mono;

public interface GodMoneyService {
    Mono<God> addMoney(GodCommand.AddMoneyCommand command);

    Mono<God> subtractMoney(GodCommand.SubtractMoneyCommand command);
}
