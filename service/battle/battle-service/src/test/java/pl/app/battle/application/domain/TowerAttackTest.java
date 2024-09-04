package pl.app.battle.application.domain;

import org.assertj.core.api.Assertions;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import pl.app.common.shared.model.Statistics;
import pl.app.item.query.dto.WeaponDto;

import java.util.Set;

class TowerAttackTest {

    @Test
    void start() {
        Set<BattleCharacter> team1 = Set.of(getCharacter1(), getCharacter2());
        Set<BattleCharacter> team2 = Set.of(monster1(), monster2(), monster3());
        TowerAttack towerAttack = new TowerAttack(ObjectId.get(), team1, team2);
        towerAttack.start();
        Assertions.assertThat(towerAttack).isNotNull();
    }

    private BattleCharacter getCharacter1() {
        return new BattleCharacter(ObjectId.get(), ObjectId.get(), BattleCharacterType.PLAYER, "WARRIOR", "AlaKot", 10, 2000L,
                new Statistics(40L, 20L, 20L, 20L, 60_000L, 50_000L, 20_000L, 20_000L),
                new Statistics(0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L),
                new Statistics(40L, 20L, 20L, 20L, 60_000L, 50_000L, 20_000L, 20_000L),
                10_000L, 92L, 80L,
                new WeaponDto(10L, 10L),
                null);
    }
    private BattleCharacter getCharacter2() {
        return new BattleCharacter(ObjectId.get(), ObjectId.get(), BattleCharacterType.PLAYER, "WARRIOR", "AlaKot", 10, 2000L,
                new Statistics(40L, 20L, 20L, 20L, 60_000L, 50_000L, 20_000L, 20_000L),
                new Statistics(0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L),
                new Statistics(40L, 20L, 20L, 20L, 60_000L, 50_000L, 20_000L, 20_000L),
                10_000L, 92L, 80L,
                new WeaponDto(10L, 10L),
                null);
    }
    private BattleCharacter monster1() {
        return new BattleCharacter(ObjectId.get(), ObjectId.get(), BattleCharacterType.MONSTER, "WARRIOR", "AlaKot2", 10, 2000L,
                new Statistics(40L, 20L, 20L, 19L, 60_000L, 50_000L, 20_000L, 20_000L),
                new Statistics(0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L),
                new Statistics(40L, 20L, 20L, 19L, 60_000L, 50_000L, 20_000L, 20_000L),
                2_000L, 92L, 80L,
                new WeaponDto(2L, 5L),
                null);
    }
    private BattleCharacter monster2() {
        return new BattleCharacter(ObjectId.get(), ObjectId.get(), BattleCharacterType.MONSTER, "WARRIOR", "AlaKot2", 10, 2000L,
                new Statistics(40L, 20L, 20L, 19L, 60_000L, 50_000L, 20_000L, 20_000L),
                new Statistics(0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L),
                new Statistics(40L, 20L, 20L, 19L, 60_000L, 50_000L, 20_000L, 20_000L),
                2_000L, 92L, 80L,
                new WeaponDto(2L, 5L),
                null);
    }
    private BattleCharacter monster3() {
        return new BattleCharacter(ObjectId.get(), ObjectId.get(), BattleCharacterType.MONSTER, "WARRIOR", "AlaKot2", 10, 2000L,
                new Statistics(40L, 20L, 20L, 19L, 60_000L, 50_000L, 20_000L, 20_000L),
                new Statistics(0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L),
                new Statistics(40L, 20L, 20L, 19L, 60_000L, 50_000L, 20_000L, 20_000L),
                2_000L, 92L, 80L,
                new WeaponDto(2L, 5L),
                null);
    }
}