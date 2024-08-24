package pl.app.battle.application.domain;

import org.assertj.core.api.Assertions;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import pl.app.common.shared.model.Statistics;
import pl.app.item.query.dto.WeaponDto;

import java.util.Set;

class BattleTest {

    @Test
    void startBattle() {
        var ch1 = new BattleCharacter(ObjectId.get(), ObjectId.get(),BattleCharacterType.PLAYER, "WARRIOR", "AlaKot", 10, 2000L,
                new Statistics(40L, 20L, 20L, 19L, 60_000L, 50_000L, 20_000L, 20_000L),
                new Statistics(0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L),
                new Statistics(40L, 20L, 20L, 19L, 60_000L, 50_000L, 20_000L, 20_000L),
                920L, 92L, 80L,
                new WeaponDto(20L, 20L),
                null);
        var ch2 = new BattleCharacter(ObjectId.get(), ObjectId.get(),BattleCharacterType.PLAYER, "WARRIOR", "AlaKot2", 10, 2000L,
                new Statistics(40L, 20L, 20L, 19L, 60_000L, 50_000L, 20_000L, 20_000L),
                new Statistics(0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L),
                new Statistics(40L, 20L, 20L, 19L, 60_000L, 50_000L, 20_000L, 20_000L),
                9220L, 92L, 80L,
                new WeaponDto(20L, 20L),
                null);
        Battle battle = new Battle(Set.of(ch1), Set.of(ch2));
        battle.startBattle();
        BattleResult battleResult = battle.getBattleResult();
        Assertions.assertThat(battleResult).isNotNull();
    }
}