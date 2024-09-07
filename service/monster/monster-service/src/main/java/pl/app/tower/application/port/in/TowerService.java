package pl.app.tower.application.port.in;

import pl.app.monster_template.application.domain.MonsterTemplate;
import pl.app.monster_template.in.MonsterTemplateCommand;
import pl.app.tower.application.domain.TowerLevel;
import pl.app.tower.in.TowerCommand;
import reactor.core.publisher.Mono;


public interface TowerService {
    Mono<TowerLevel> create(TowerCommand.CreateTowerLevelCommand command);

    Mono<TowerLevel> update(TowerCommand.UpdateTowerLevelCommand command);

    Mono<TowerLevel> remove(TowerCommand.RemoveTowerLevelCommand command);
}
