package pl.app.battle.application.domain;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import pl.app.battle.application.domain.battle.BattleCharacter;
import pl.app.battle.application.domain.battle.BattleCharacterType;
import pl.app.battle.application.domain.tower_attack.TowerAttack;
import pl.app.battle.application.domain.tower_attack.TowerAttackResult;
import pl.app.common.shared.model.Statistics;
import pl.app.item.query.dto.WeaponDto;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.Set;

class TowerAttackTest {

    @Test
    void start() throws InterruptedException {
        Set<BattleCharacter> team1 = Set.of(getCharacter1(), getCharacter2());
        Set<BattleCharacter> team2 = Set.of(monster1(), monster2(), monster3());

        Thread.sleep(20_000);
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