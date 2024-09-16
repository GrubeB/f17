package pl.app.tower_attack.application.domain;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import pl.app.unit.application.domain.BattleCharacter;
import pl.app.common.shared.model.CharacterProfession;
import pl.app.common.shared.model.Statistics;
import pl.app.item.query.dto.WeaponDto;

import java.util.Set;

class TowerAttackTest {

    @Test
    void start() throws InterruptedException {
        Set<BattleCharacter> team1 = Set.of(getCharacter1(), getCharacter2());
        Set<BattleCharacter> team2 = Set.of(monster1(), monster2(), monster3());

        Thread.sleep(20_000);
    }
    private BattleCharacter getCharacter1() {
        return new BattleCharacter(CharacterProfession.WARRIOR,
                "AlaKot", 10, 2000L,
                new Statistics(40L, 20L, 20L, 20L, 60_000L, 50_000L, 20_000L, 20_000L),
                5920L, 92L, 80L,
                new WeaponDto(20L, 20L),new WeaponDto(20L, 20L),
                ObjectId.get(), ObjectId.get());
    }

    private BattleCharacter getCharacter2() {
        return new BattleCharacter(CharacterProfession.WARRIOR,
                "AlaKot", 10, 2000L,
                new Statistics(40L, 20L, 20L, 20L, 60_000L, 50_000L, 20_000L, 20_000L),
                5920L, 92L, 80L,
                new WeaponDto(20L, 20L),new WeaponDto(20L, 20L),
                ObjectId.get(), ObjectId.get());
    }

    private BattleCharacter monster1() {
        return new BattleCharacter(CharacterProfession.WARRIOR,
                "AlaKot", 10, 2000L,
                new Statistics(40L, 20L, 20L, 20L, 60_000L, 50_000L, 20_000L, 20_000L),
                5920L, 92L, 80L,
                new WeaponDto(20L, 20L),new WeaponDto(20L, 20L),
                ObjectId.get(), ObjectId.get());
    }

    private BattleCharacter monster2() {
        return new BattleCharacter(CharacterProfession.WARRIOR,
                "AlaKot", 10, 2000L,
                new Statistics(40L, 20L, 20L, 20L, 60_000L, 50_000L, 20_000L, 20_000L),
                5920L, 92L, 80L,
                new WeaponDto(20L, 20L),new WeaponDto(20L, 20L),
                ObjectId.get(), ObjectId.get());
    }

    private BattleCharacter monster3() {
        return new BattleCharacter(CharacterProfession.WARRIOR,
                "AlaKot", 10, 2000L,
                new Statistics(40L, 20L, 20L, 20L, 60_000L, 50_000L, 20_000L, 20_000L),
                5920L, 92L, 80L,
                new WeaponDto(20L, 20L),new WeaponDto(20L, 20L),
                ObjectId.get(), ObjectId.get());
    }
}