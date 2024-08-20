package pl.app.character.application.port.in;

import pl.app.character.application.domain.Character;
import reactor.core.publisher.Mono;

public interface CharacterMoneyService {
    Mono<Character> addMoney(CharacterCommand.AddMoneyCommand command);
    Mono<Character> subtractMoney(CharacterCommand.SubtractMoneyCommand command);
}
