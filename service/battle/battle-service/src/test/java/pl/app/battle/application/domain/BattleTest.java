package pl.app.battle.application.domain;

import org.assertj.core.api.Assertions;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import pl.app.unit.application.domain.BattleCharacter;
import pl.app.unit.application.domain.BattleUnitType;
import pl.app.common.shared.model.CharacterProfession;
import pl.app.common.shared.model.Statistics;
import pl.app.item.query.dto.WeaponDto;

import java.util.Set;

class BattleTest {

    @Test
    void startBattle() {
        var ch1 = new BattleCharacter(CharacterProfession.WARRIOR,
                "AlaKot", 10, 2000L,
                new Statistics(40L, 20L, 20L, 20L, 60_000L, 50_000L, 20_000L, 20_000L),
                5920L, 92L, 80L,
                new WeaponDto(20L, 20L),new WeaponDto(20L, 20L),
                ObjectId.get(), ObjectId.get());
        var ch2 = new BattleCharacter(CharacterProfession.WARRIOR,
                "AlaKot", 10, 2000L,
                new Statistics(40L, 20L, 20L, 20L, 60_000L, 50_000L, 20_000L, 20_000L),
                5920L, 92L, 80L,
                new WeaponDto(20L, 20L),new WeaponDto(20L, 20L),
                ObjectId.get(), ObjectId.get());
        Battle battle = new Battle(Set.of(ch1), Set.of(ch2));
        battle.startBattle();
        BattleResult battleResult = battle.getFinishManager().getBattleResult();
        Assertions.assertThat(battleResult).isNotNull();
    }
}