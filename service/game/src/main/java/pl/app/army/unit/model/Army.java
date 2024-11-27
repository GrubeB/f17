package pl.app.army.unit.model;

import lombok.Getter;
import lombok.ToString;

import java.util.*;
import java.util.stream.Collectors;

import static pl.app.army.unit.model.UnitType.*;

@Getter
@ToString
public class Army {
    protected Integer spearmanNumber;
    protected Integer swordsmanNumber;
    protected Integer archerNumber;
    protected Integer heavyCavalryNumber;
    protected Integer axeFighterNumber;
    protected Integer lightCavalryNumber;
    protected Integer mountedArcherNumber;
    protected Integer ramNumber;
    protected Integer catapultNumber;
    protected Integer paladinNumber;
    protected Integer noblemanNumber;
    protected Integer berserkerNumber;
    protected Integer trebuchetNumber;

    protected Army() {
        this.spearmanNumber = 0;
        this.swordsmanNumber = 0;
        this.archerNumber = 0;
        this.heavyCavalryNumber = 0;
        this.axeFighterNumber = 0;
        this.lightCavalryNumber = 0;
        this.mountedArcherNumber = 0;
        this.ramNumber = 0;
        this.catapultNumber = 0;
        this.paladinNumber = 0;
        this.noblemanNumber = 0;
        this.berserkerNumber = 0;
        this.trebuchetNumber = 0;
    }

    protected Army(Army army) {
        this.spearmanNumber = Objects.nonNull(army.spearmanNumber) ? army.spearmanNumber : 0;
        this.swordsmanNumber = Objects.nonNull(army.swordsmanNumber) ? army.swordsmanNumber : 0;
        this.archerNumber = Objects.nonNull(army.archerNumber) ? army.archerNumber : 0;
        this.heavyCavalryNumber = Objects.nonNull(army.heavyCavalryNumber) ? army.heavyCavalryNumber : 0;
        this.axeFighterNumber = Objects.nonNull(army.axeFighterNumber) ? army.axeFighterNumber : 0;
        this.lightCavalryNumber = Objects.nonNull(army.lightCavalryNumber) ? army.lightCavalryNumber : 0;
        this.mountedArcherNumber = Objects.nonNull(army.mountedArcherNumber) ? army.mountedArcherNumber : 0;
        this.ramNumber = Objects.nonNull(army.ramNumber) ? army.ramNumber : 0;
        this.catapultNumber = Objects.nonNull(army.catapultNumber) ? army.catapultNumber : 0;
        this.paladinNumber = Objects.nonNull(army.paladinNumber) ? army.paladinNumber : 0;
        this.noblemanNumber = Objects.nonNull(army.noblemanNumber) ? army.noblemanNumber : 0;
        this.berserkerNumber = Objects.nonNull(army.berserkerNumber) ? army.berserkerNumber : 0;
        this.trebuchetNumber = Objects.nonNull(army.trebuchetNumber) ? army.trebuchetNumber : 0;

    }

    protected Army(Integer spearmanNumber, Integer swordsmanNumber, Integer archerNumber, Integer heavyCavalryNumber, Integer axeFighterNumber, Integer lightCavalryNumber, Integer mountedArcherNumber, Integer ramNumber, Integer catapultNumber, Integer paladinNumber, Integer noblemanNumber, Integer berserkerNumber, Integer trebuchetNumber) {
        this.spearmanNumber = Objects.nonNull(spearmanNumber) ? spearmanNumber : 0;
        this.swordsmanNumber = Objects.nonNull(swordsmanNumber) ? swordsmanNumber : 0;
        this.archerNumber = Objects.nonNull(archerNumber) ? archerNumber : 0;
        this.heavyCavalryNumber = Objects.nonNull(heavyCavalryNumber) ? heavyCavalryNumber : 0;
        this.axeFighterNumber = Objects.nonNull(axeFighterNumber) ? axeFighterNumber : 0;
        this.lightCavalryNumber = Objects.nonNull(lightCavalryNumber) ? lightCavalryNumber : 0;
        this.mountedArcherNumber = Objects.nonNull(mountedArcherNumber) ? mountedArcherNumber : 0;
        this.ramNumber = Objects.nonNull(ramNumber) ? ramNumber : 0;
        this.catapultNumber = Objects.nonNull(catapultNumber) ? catapultNumber : 0;
        this.paladinNumber = Objects.nonNull(paladinNumber) ? paladinNumber : 0;
        this.noblemanNumber = Objects.nonNull(noblemanNumber) ? noblemanNumber : 0;
        this.berserkerNumber = Objects.nonNull(berserkerNumber) ? berserkerNumber : 0;
        this.trebuchetNumber = Objects.nonNull(trebuchetNumber) ? trebuchetNumber : 0;

    }

    protected Army(Map<UnitType, Integer> units) {
        this(
                Objects.nonNull(units.get(SPEARMAN)) ? units.get(SPEARMAN) : 0,
                Objects.nonNull(units.get(SWORDSMAN)) ? units.get(SWORDSMAN) : 0,
                Objects.nonNull(units.get(ARCHER)) ? units.get(ARCHER) : 0,
                Objects.nonNull(units.get(HEAVY_CAVALRY)) ? units.get(HEAVY_CAVALRY) : 0,
                Objects.nonNull(units.get(AXE_FIGHTER)) ? units.get(AXE_FIGHTER) : 0,
                Objects.nonNull(units.get(LIGHT_CAVALRY)) ? units.get(LIGHT_CAVALRY) : 0,
                Objects.nonNull(units.get(MOUNTED_ARCHER)) ? units.get(MOUNTED_ARCHER) : 0,
                Objects.nonNull(units.get(RAM)) ? units.get(RAM) : 0,
                Objects.nonNull(units.get(CATAPULT)) ? units.get(CATAPULT) : 0,
                Objects.nonNull(units.get(PALADIN)) ? units.get(PALADIN) : 0,
                Objects.nonNull(units.get(NOBLEMAN)) ? units.get(NOBLEMAN) : 0,
                Objects.nonNull(units.get(BERSERKER)) ? units.get(BERSERKER) : 0,
                Objects.nonNull(units.get(TREBUCHET)) ? units.get(TREBUCHET) : 0
        );
    }

    public static Army zero() {
        return new Army(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
    }

    public static Army of(Integer spearmanNumber, Integer swordsmanNumber, Integer archerNumber, Integer heavyCavalryNumber, Integer axeFighterNumber, Integer lightCavalryNumber, Integer mountedArcherNumber, Integer ramNumber, Integer catapultNumber, Integer paladinNumber, Integer noblemanNumber, Integer berserkerNumber, Integer trebuchetNumber) {
        return new Army(spearmanNumber, swordsmanNumber, archerNumber, heavyCavalryNumber, axeFighterNumber, lightCavalryNumber, mountedArcherNumber, ramNumber, catapultNumber, paladinNumber, noblemanNumber, berserkerNumber, trebuchetNumber);
    }

    public static Army of(Map<UnitType, Integer> units) {
        return new Army(units);
    }

    public static Army of(Army army) {
        return new Army(army);
    }

    public Map<UnitType, Integer> army() {
        Map<UnitType, Integer> army = EnumSet.allOf(UnitType.class).stream()
                .collect(Collectors.toMap(t -> t, t -> 0, (o, n) -> n, HashMap::new));
        army.put(SPEARMAN, spearmanNumber);
        army.put(SWORDSMAN, swordsmanNumber);
        army.put(ARCHER, archerNumber);
        army.put(HEAVY_CAVALRY, heavyCavalryNumber);
        army.put(AXE_FIGHTER, axeFighterNumber);
        army.put(LIGHT_CAVALRY, lightCavalryNumber);
        army.put(MOUNTED_ARCHER, mountedArcherNumber);
        army.put(RAM, ramNumber);
        army.put(CATAPULT, catapultNumber);
        army.put(PALADIN, paladinNumber);
        army.put(NOBLEMAN, noblemanNumber);
        army.put(BERSERKER, berserkerNumber);
        army.put(TREBUCHET, trebuchetNumber);
        return army;
    }

    public Set<Map.Entry<UnitType, Integer>> entrySet() {
        return army().entrySet();
    }

    public boolean isEmpty() {
        return army().values().stream().allMatch(i -> i <= 0);
    }

    public void add(Army army) {
        spearmanNumber += army.spearmanNumber;
        swordsmanNumber += army.swordsmanNumber;
        archerNumber += army.archerNumber;
        heavyCavalryNumber += army.heavyCavalryNumber;
        axeFighterNumber += army.axeFighterNumber;
        lightCavalryNumber += army.lightCavalryNumber;
        mountedArcherNumber += army.mountedArcherNumber;
        ramNumber += army.ramNumber;
        catapultNumber += army.catapultNumber;
        paladinNumber += army.paladinNumber;
        noblemanNumber += army.noblemanNumber;
        berserkerNumber += army.berserkerNumber;
        trebuchetNumber += army.trebuchetNumber;
    }

    public void subtract(Army army) {
        spearmanNumber -= army.spearmanNumber;
        swordsmanNumber -= army.swordsmanNumber;
        archerNumber -= army.archerNumber;
        heavyCavalryNumber -= army.heavyCavalryNumber;
        axeFighterNumber -= army.axeFighterNumber;
        lightCavalryNumber -= army.lightCavalryNumber;
        mountedArcherNumber -= army.mountedArcherNumber;
        ramNumber -= army.ramNumber;
        catapultNumber -= army.catapultNumber;
        paladinNumber -= army.paladinNumber;
        noblemanNumber -= army.noblemanNumber;
        berserkerNumber -= army.berserkerNumber;
        trebuchetNumber -= army.trebuchetNumber;
    }

    public void multiply(Double number) {
        spearmanNumber = (int) (spearmanNumber * number);
        swordsmanNumber = (int) (swordsmanNumber * number);
        archerNumber = (int) (archerNumber * number);
        heavyCavalryNumber = (int) (heavyCavalryNumber * number);
        axeFighterNumber = (int) (axeFighterNumber * number);
        lightCavalryNumber = (int) (lightCavalryNumber * number);
        mountedArcherNumber = (int) (mountedArcherNumber * number);
        ramNumber = (int) (ramNumber * number);
        catapultNumber = (int) (catapultNumber * number);
        paladinNumber = (int) (paladinNumber * number);
        noblemanNumber = (int) (noblemanNumber * number);
        berserkerNumber = (int) (berserkerNumber * number);
        trebuchetNumber = (int) (trebuchetNumber * number);
    }

    public void multiply(Map<UnitType, Double> ratios) {
        spearmanNumber = (int) (spearmanNumber * ratios.get(SPEARMAN));
        swordsmanNumber = (int) (swordsmanNumber * ratios.get(SWORDSMAN));
        archerNumber = (int) (archerNumber * ratios.get(ARCHER));
        heavyCavalryNumber = (int) (heavyCavalryNumber * ratios.get(HEAVY_CAVALRY));
        axeFighterNumber = (int) (axeFighterNumber * ratios.get(AXE_FIGHTER));
        lightCavalryNumber = (int) (lightCavalryNumber * ratios.get(LIGHT_CAVALRY));
        mountedArcherNumber = (int) (mountedArcherNumber * ratios.get(MOUNTED_ARCHER));
        ramNumber = (int) (ramNumber * ratios.get(RAM));
        catapultNumber = (int) (catapultNumber * ratios.get(CATAPULT));
        paladinNumber = (int) (paladinNumber * ratios.get(PALADIN));
        noblemanNumber = (int) (noblemanNumber * ratios.get(NOBLEMAN));
        berserkerNumber = (int) (berserkerNumber * ratios.get(BERSERKER));
        trebuchetNumber = (int) (trebuchetNumber * ratios.get(TREBUCHET));
    }

    public void toZero() {
        this.spearmanNumber = 0;
        this.swordsmanNumber = 0;
        this.archerNumber = 0;
        this.heavyCavalryNumber = 0;
        this.axeFighterNumber = 0;
        this.lightCavalryNumber = 0;
        this.mountedArcherNumber = 0;
        this.ramNumber = 0;
        this.catapultNumber = 0;
        this.paladinNumber = 0;
        this.noblemanNumber = 0;
        this.berserkerNumber = 0;
        this.trebuchetNumber = 0;
    }

    public int get(UnitType unitType) {
        return army().get(unitType);
    }

}
