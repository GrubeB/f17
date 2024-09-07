package pl.app.monster.application.port.in;

import pl.app.monster.application.domain.Monster;
import reactor.core.publisher.Mono;

public interface MonsterService {
    Mono<Monster> create(MonsterCommand.CreateMonsterCommand command);
    Mono<Monster> remove(MonsterCommand.RemoveMonsterCommand command);
}
