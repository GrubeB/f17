package pl.app.battle.application.domain;

import org.assertj.core.api.Assertions;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;

import java.util.Set;

class BattleTest {

    @Test
    void startBattle() {
        var ch1 = new BattleCharacter(ObjectId.get(), BattleCharacterType.PLAYER, "WARRIOR", "CH1", new BattleCharacterStatistics(
                40L, 20L, 20L, 19L, 60_000L, 50_000L, 20_000L, 20_000L
        ));
        var ch2 = new BattleCharacter(ObjectId.get(), BattleCharacterType.PLAYER, "WARRIOR", "CH2", new BattleCharacterStatistics(
                50L, 20L, 30L, 31L, 60_000L, 50_000L, 20_000L, 20_000L
        ));
        Battle battle = new Battle(Set.of(ch1), Set.of(ch2));
        battle.startBattle();
        BattleResult battleResult = battle.getBattleResult();
        Assertions.assertThat(battleResult).isNotNull();
    }
}