package pl.app.attack.battle.application.domain;

import org.junit.jupiter.api.Test;
import pl.app.attack.battle.application.domain.Battle;
import pl.app.unit.unit.application.domain.Army;
import pl.app.unit.unit.application.domain.Unit;
import pl.app.unit.unit.application.domain.UnitType;
import pl.app.unit.unit.application.port.in.UnitDomainRepositoryImpl;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.app.unit.unit.application.domain.UnitType.*;

public class BattleTest {

    private final UnitDomainRepositoryImpl unitDomainRepository = new UnitDomainRepositoryImpl();
    private final Map<UnitType, Unit> units = unitDomainRepository.fetchAll().block();

    @Test
    void test1() {
        var army1 = EnumSet.allOf(UnitType.class).stream().collect(Collectors.toMap(t -> t, t -> 0, (o, n) -> n, HashMap::new));
        army1.put(SPEARMAN, 200);
        var army2 = EnumSet.allOf(UnitType.class).stream().collect(Collectors.toMap(t -> t, t -> 0, (o, n) -> n, HashMap::new));
        army2.put(ARCHER, 100);
        Battle battle = new Battle(Army.of(army1), Army.of(army2), units, 0);
        battle.setBattleBuffs(true, true, 100, 0, false, false);
        battle.startBattle();
        assertThat(battle.getAttackerArmy().getArmyProvisions()).isEqualTo(200 - 71);
        assertThat(battle.getDefenderArmy().getArmyProvisions()).isEqualTo(0);
    }

    @Test
    void test2() {
        var army1 = EnumSet.allOf(UnitType.class).stream().collect(Collectors.toMap(t -> t, t -> 0, (o, n) -> n, HashMap::new));
        army1.put(SPEARMAN, 100);
        var army2 = EnumSet.allOf(UnitType.class).stream().collect(Collectors.toMap(t -> t, t -> 0, (o, n) -> n, HashMap::new));
        army2.put(ARCHER, 200);
        Battle battle = new Battle(Army.of(army1), Army.of(army2), units, 0);
        battle.startBattle();
        assertThat(battle.getAttackerArmy().getArmyProvisions()).isEqualTo(0);
        assertThat(battle.getDefenderArmy().getArmyProvisions()).isEqualTo(129);
    }

    @Test
    void test3() {
        var army1 = EnumSet.allOf(UnitType.class).stream().collect(Collectors.toMap(t -> t, t -> 0, (o, n) -> n, HashMap::new));
        army1.put(AXE_FIGHTER, 200);
        army1.put(LIGHT_CAVALRY, 100);
        var army2 = EnumSet.allOf(UnitType.class).stream().collect(Collectors.toMap(t -> t, t -> 0, (o, n) -> n, HashMap::new));
        army2.put(SPEARMAN, 100);
        army2.put(SWORDSMAN, 100);
        army2.put(ARCHER, 100);
        Battle battle = new Battle(Army.of(army1), Army.of(army2), units, 0);
        battle.startBattle();
        assertThat(battle.getAttackerArmy().get(AXE_FIGHTER)).isEqualTo(200 - 38);
        assertThat(battle.getAttackerArmy().get(LIGHT_CAVALRY)).isEqualTo(100 - 26);
        assertThat(battle.getDefenderArmy().getArmyProvisions()).isEqualTo(0);
    }

    @Test
    void test5() {
        var army1 = EnumSet.allOf(UnitType.class).stream().collect(Collectors.toMap(t -> t, t -> 0, (o, n) -> n, HashMap::new));
        army1.put(SPEARMAN, 200);
        var army2 = EnumSet.allOf(UnitType.class).stream().collect(Collectors.toMap(t -> t, t -> 0, (o, n) -> n, HashMap::new));
        army2.put(ARCHER, 100);
        Battle battle = new Battle(Army.of(army1), Army.of(army2), units, 5);
        battle.setBattleBuffs(true, true, 100, 0, false, false);
        battle.startBattle();
        assertThat(battle.getAttackerArmy().getArmyProvisions()).isEqualTo(200 - 106);
        assertThat(battle.getDefenderArmy().getArmyProvisions()).isEqualTo(0);
    }

    @Test
    void test6() {
        var army1 = EnumSet.allOf(UnitType.class).stream().collect(Collectors.toMap(t -> t, t -> 0, (o, n) -> n, HashMap::new));
        army1.put(SPEARMAN, 196);
        army1.put(RAM, 20);
        var army2 = EnumSet.allOf(UnitType.class).stream().collect(Collectors.toMap(t -> t, t -> 0, (o, n) -> n, HashMap::new));
        army2.put(ARCHER, 100);
        Battle battle = new Battle(Army.of(army1), Army.of(army2), units, 5);
        battle.setBattleBuffs(true, true, 100, 0, false, false);
        battle.startBattle();
        assertThat(battle.getAttackerArmy().get(SPEARMAN)).isEqualTo(196 - 90);
        assertThat(battle.getAttackerArmy().get(RAM)).isEqualTo(20 - 9);
        assertThat(battle.getWall().getResultingWallLevel()).isEqualTo(2);
        assertThat(battle.getDefenderArmy().getArmyProvisions()).isEqualTo(0);
    }

    @Test
    void test8() {
        var army1 = EnumSet.allOf(UnitType.class).stream().collect(Collectors.toMap(t -> t, t -> 0, (o, n) -> n, HashMap::new));
        army1.put(SPEARMAN, 200);
        army1.put(RAM, 168);
        var army2 = EnumSet.allOf(UnitType.class).stream().collect(Collectors.toMap(t -> t, t -> 0, (o, n) -> n, HashMap::new));
        army2.put(ARCHER, 100);
        Battle battle = new Battle(Army.of(army1), Army.of(army2), units, 14);
        battle.setBattleBuffs(true, true, 100, 0, false, false);
        Battle.BattleResult battleResult = battle.startBattle();
        assertThat(battle.getWall().getResultingWallLevel()).isEqualTo(8);
        assertThat(battleResult.getFinishWallLevel()).isEqualTo(8);
    }
}