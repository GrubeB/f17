package pl.app.monster_template.application.port.in;

import pl.app.monster_template.application.domain.MonsterTemplate;
import pl.app.monster_template.in.MonsterTemplateCommand;
import reactor.core.publisher.Mono;


public interface MonsterTemplateService {
    Mono<MonsterTemplate> create(MonsterTemplateCommand.CreateMonsterTemplateCommand command);

    Mono<MonsterTemplate> update(MonsterTemplateCommand.UpdateMonsterTemplateCommand command);

    Mono<MonsterTemplate> remove(MonsterTemplateCommand.RemoveMonsterTemplateCommand command);
}
