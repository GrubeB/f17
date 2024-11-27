package pl.app.army.unit.model;

import lombok.Getter;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static pl.app.army.unit.model.AttackType.*;

@Getter
public enum UnitType {
    SPEARMAN(INFANTRY),
    SWORDSMAN(INFANTRY),
    ARCHER(ARCHERS),
    HEAVY_CAVALRY(CAVALRY),
    AXE_FIGHTER(INFANTRY),
    LIGHT_CAVALRY(CAVALRY),
    MOUNTED_ARCHER(ARCHERS),
    RAM(INFANTRY),
    CATAPULT(INFANTRY),
    PALADIN(CAVALRY),
    NOBLEMAN(INFANTRY),
    BERSERKER(INFANTRY),
    TREBUCHET(INFANTRY);

    private AttackType attackType;

    UnitType(AttackType attackType) {
        this.attackType = attackType;
    }

    public static Set<UnitType> getTypes(AttackType attackType) {
        return EnumSet.allOf(UnitType.class).stream()
                .filter(t -> Objects.equals(t.getAttackType(), attackType))
                .collect(Collectors.toSet());
    }

    public static Set<UnitType> getInfantryTypes() {
        return EnumSet.allOf(UnitType.class).stream()
                .filter(t -> Objects.equals(t.getAttackType(), INFANTRY))
                .collect(Collectors.toSet());
    }

    public static Set<UnitType> getCavalryTypes() {
        return EnumSet.allOf(UnitType.class).stream()
                .filter(t -> Objects.equals(t.getAttackType(), CAVALRY))
                .collect(Collectors.toSet());
    }

    public static Set<UnitType> getArchersTypes() {
        return EnumSet.allOf(UnitType.class).stream()
                .filter(t -> Objects.equals(t.getAttackType(), ARCHERS))
                .collect(Collectors.toSet());
    }

    public static boolean isInfantry(UnitType unitType) {
        return getInfantryTypes().contains(unitType);
    }

    public static boolean isCavalry(UnitType unitType) {
        return getCavalryTypes().contains(unitType);
    }

    public static boolean isArchers(UnitType unitType) {
        return getArchersTypes().contains(unitType);
    }

    public boolean isInfantry() {
        return getAttackType().equals(INFANTRY);
    }

    public boolean isCavalry() {
        return getAttackType().equals(CAVALRY);
    }

    public boolean isArchers() {
        return getAttackType().equals(ARCHERS);
    }
}
