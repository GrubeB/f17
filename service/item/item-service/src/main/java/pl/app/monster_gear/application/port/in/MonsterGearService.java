package pl.app.monster_gear.application.port.in;

import pl.app.god_equipment.application.domain.CharacterGear;
import pl.app.god_equipment.application.domain.GodEquipment;
import pl.app.monster_gear.application.domain.MonsterGear;
import reactor.core.publisher.Mono;

public interface MonsterGearService {
    Mono<MonsterGear> crateMonsterGear(MonsterGearCommand.CreateMonsterGearCommand command);
    Mono<MonsterGear> removeMonsterGear(MonsterGearCommand.RemoveMonsterGearCommand command);
    Mono<MonsterGear> setMonsterItem(MonsterGearCommand.SetMonsterItemCommand command);
    Mono<MonsterGear> removeMonsterItem(MonsterGearCommand.RemoveMonsterItemCommand command);
}
