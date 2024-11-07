package pl.app.attack.battle.application.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.app.unit.unit.application.domain.Army;
import pl.app.unit.unit.application.domain.Unit;
import pl.app.unit.unit.application.domain.UnitType;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static pl.app.unit.unit.application.domain.UnitType.RAM;
import static pl.app.unit.unit.application.domain.UnitType.TREBUCHET;

/**
 * BUFF         TRUE/FALSE
 * BONUS        0 - 100
 * MODIFIER     0.0 - 2.0
 */
@Getter
public class Battle {
    private static final Logger logger = LoggerFactory.getLogger(Battle.class);
    private static final Integer MAX_NUMBER_OF_TURNS = 10;
    private ObjectId battleId;
    private BattleArmy originalAttackerArmy;
    private BattleArmy attackerArmy;
    private BattleArmy originalDefenderArmy;
    private BattleArmy defenderArmy;
    private Map<UnitType, Unit> units;
    private Wall wall;
    private BattleModifier battleModifier;
    private BattleResult battleResult;
    private Integer turnCounter = 0;

    public Battle() {
    }

    public Battle(Army attackerArmy, Army defenderArmy, Map<UnitType, Unit> units, Integer inputWallLevel) {
        this.battleId = ObjectId.get();
        this.originalAttackerArmy = new BattleArmy(attackerArmy);
        this.attackerArmy = new BattleArmy(attackerArmy);
        this.originalDefenderArmy = new BattleArmy(defenderArmy);
        this.defenderArmy = new BattleArmy(defenderArmy);
        this.units = units;
        this.wall = new Wall(inputWallLevel);
        battleModifier = new BattleModifier();
    }

    public void setBattleBuffs(boolean attackerFaithBonus, boolean defenderFaithBonus, int moraleBonus,
                               int luckBonus, boolean grandmasterBuff, boolean medicBuff, boolean nightBuf) {
        battleModifier = new BattleModifier(attackerFaithBonus, defenderFaithBonus, moraleBonus, luckBonus, grandmasterBuff, medicBuff, nightBuf);
    }

    public BattleResult startBattle() {
        logger.debug("starting battle");
        trebuchetTurn();
        wall.init();
        do {
            startTurn();
        } while (this.attackerArmy.getArmyProvisions() > 0 && this.defenderArmy.getArmyProvisions() > 0
                || turnCounter >= MAX_NUMBER_OF_TURNS);
        wall.finish();
        logger.debug("ended battle");
        return endBattle();
    }

    private BattleResult endBattle() {
        this.battleResult = new BattleResult(
                battleId,
                attackerArmy.getArmyProvisions() > 0,
                originalAttackerArmy,
                attackerArmy,
                originalDefenderArmy,
                defenderArmy,
                wall.getStartWallLevel(),
                wall.getResultingWallLevel()
        );
        if (battleModifier.medicBuff && battleResult.isAttackerWin()) {
            var refund = battleResult.getAttackerArmyLosses().army().entrySet().stream()
                    .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue() / 10));
            Army finalAttackerArmy = battleResult.getFinalAttackerArmy();
            finalAttackerArmy.add(Army.of(refund));
            battleResult.setFinalAttackerArmy(finalAttackerArmy);
        }
        return battleResult;
    }

    private void startTurn() {
        logger.debug("starting turn");
        BattleArmy infTurnAtcArmy = attackerArmy.getInfArmy();
        double infAtcArmyPercentage = (double) infTurnAtcArmy.getArmyProvisions() / attackerArmy.getArmyProvisions();
        BattleArmy cavTurnAtcArmy = attackerArmy.getCavArmy();
        double cavAtcArmyPercentage = (double) cavTurnAtcArmy.getArmyProvisions() / attackerArmy.getArmyProvisions();
        BattleArmy arcTurnAtcArmy = attackerArmy.getArcArmy();
        double arcAtcArmyPercentage = (double) arcTurnAtcArmy.getArmyProvisions() / attackerArmy.getArmyProvisions();

        BattleArmy infTurnDefArmy = defenderArmy.getPercentageOfArmy(infAtcArmyPercentage);
        BattleArmy cavTurnDefArmy = defenderArmy.getPercentageOfArmy(cavAtcArmyPercentage);
        BattleArmy arcTurnDefArmy = defenderArmy.getPercentageOfArmy(arcAtcArmyPercentage);

        startInfTurn(infTurnAtcArmy, infTurnDefArmy);
        startCavTurn(cavTurnAtcArmy, cavTurnDefArmy);
        startArcTurn(arcTurnAtcArmy, arcTurnDefArmy);

        BattleArmy newAtcArmy = new BattleArmy();
        newAtcArmy.add(infTurnAtcArmy);
        newAtcArmy.add(cavTurnAtcArmy);
        newAtcArmy.add(arcTurnAtcArmy);
        BattleArmy newDefArmy = new BattleArmy();
        newDefArmy.add(infTurnDefArmy);
        newDefArmy.add(cavTurnDefArmy);
        newDefArmy.add(arcTurnDefArmy);
        this.attackerArmy = newAtcArmy;
        this.defenderArmy = newDefArmy;

        turnCounter++;
        logger.debug("ended turn");
    }

    private void startInfTurn(BattleArmy infTurnAtcArmy, BattleArmy infTurnDefArmy) {
        int atcArmyAttackPower = (int) (infTurnAtcArmy.getTotalAtcPower() * battleModifier.getAttackerModifier());
        int defArmyDefPower = (int) (infTurnDefArmy.getTotalInfDefPower() * battleModifier.getDefenderModifier() + (wall.getWallDefenseBonus()));

        if (atcArmyAttackPower == defArmyDefPower) {
            infTurnDefArmy.toZero();
            infTurnAtcArmy.toZero();
        } else if (atcArmyAttackPower > defArmyDefPower) { // attacker win
            infTurnDefArmy.toZero();
            double powerToSubtract = Math.sqrt((double) defArmyDefPower / atcArmyAttackPower) * defArmyDefPower;
            infTurnAtcArmy.subtractBasedOnDefPower(powerToSubtract);
        } else { // def win
            infTurnAtcArmy.toZero();
            double powerToSubtract = Math.sqrt((double) atcArmyAttackPower / defArmyDefPower) * atcArmyAttackPower;
            infTurnDefArmy.subtractBasedOnInfAtcPower(powerToSubtract);
        }
    }

    private void startCavTurn(BattleArmy infTurnAtcArmy, BattleArmy infTurnDefArmy) {
        int atcArmyAttackPower = (int) (infTurnAtcArmy.getTotalAtcPower() * battleModifier.getAttackerModifier());
        int defArmyDefPower = (int) (infTurnDefArmy.getTotalCavDefPower() * battleModifier.getDefenderModifier() + (wall.getWallDefenseBonus() * battleModifier.getDefenderModifier()));

        if (atcArmyAttackPower == defArmyDefPower) {
            infTurnDefArmy.toZero();
            infTurnAtcArmy.toZero();
        } else if (atcArmyAttackPower > defArmyDefPower) { // attacker win
            infTurnDefArmy.toZero();
            double powerToSubtract = Math.sqrt((double) defArmyDefPower / atcArmyAttackPower) * defArmyDefPower;
            infTurnAtcArmy.subtractBasedOnDefPower(powerToSubtract);
        } else { // def win
            infTurnAtcArmy.toZero();
            double powerToSubtract = Math.sqrt((double) atcArmyAttackPower / defArmyDefPower) * atcArmyAttackPower;
            infTurnDefArmy.subtractBasedOnCavAtcPower(powerToSubtract);
        }
    }

    private void startArcTurn(BattleArmy infTurnAtcArmy, BattleArmy infTurnDefArmy) {
        int atcArmyAttackPower = (int) (infTurnAtcArmy.getTotalAtcPower() * battleModifier.getAttackerModifier());
        int defArmyDefPower = (int) (infTurnDefArmy.getTotalArcDefPower() * battleModifier.getDefenderModifier() + (wall.getWallDefenseBonus() * battleModifier.getDefenderModifier()));

        if (atcArmyAttackPower == defArmyDefPower) {
            infTurnDefArmy.toZero();
            infTurnAtcArmy.toZero();
        } else if (atcArmyAttackPower > defArmyDefPower) { // attacker win
            infTurnDefArmy.toZero();
            double powerToSubtract = Math.sqrt((double) defArmyDefPower / atcArmyAttackPower) * defArmyDefPower;
            infTurnAtcArmy.subtractBasedOnDefPower(powerToSubtract);
        } else { // def win
            infTurnAtcArmy.toZero();
            double powerToSubtract = Math.sqrt((double) atcArmyAttackPower / defArmyDefPower) * atcArmyAttackPower;
            infTurnDefArmy.subtractBasedOnArcAtcPower(powerToSubtract);
        }
    }

    private void trebuchetTurn() {
        if (attackerArmy.get(RAM) > 0) {
            var startRamNumber = attackerArmy.get(RAM);
            var ramNumberToDestroy = defenderArmy.get(TREBUCHET);
            if (ramNumberToDestroy > startRamNumber) {
                ramNumberToDestroy = startRamNumber;
            }
            attackerArmy.subtract(Army.of(Map.of(RAM, ramNumberToDestroy)));
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BattleResult {
        private ObjectId battleId;
        private boolean attackerWin;
        private Army originalAttackerArmy;
        private Army finalAttackerArmy;
        private Army originalDefenderArmy;
        private Army finalDefenderArmy;
        private Integer originalWallLevel;
        private Integer finishWallLevel;

        public Army getAttackerArmyLosses() {
            Army losses = Army.of(originalAttackerArmy);
            losses.subtract(finalAttackerArmy);
            return losses;
        }

        public Army getDefenderArmyLosses() {
            Army losses = Army.of(originalDefenderArmy);
            losses.subtract(finalDefenderArmy);
            return losses;
        }

        public Integer getNumberOfWallLevelDestroyed() {
            return originalWallLevel - finishWallLevel;
        }
    }

    @Getter
    public class BattleModifier {
        private boolean attackerFaithBuff;
        private boolean defenderFaithBuff;
        private int moraleBonus;
        private int luckBonus; // -15% and +15%
        private boolean grandmasterBuff;
        private boolean medicBuff;
        private boolean nightBuff;

        public BattleModifier() {
            this.attackerFaithBuff = true;
            this.defenderFaithBuff = true;
            this.moraleBonus = 100;
            this.luckBonus = 0;
            this.grandmasterBuff = false;
            this.medicBuff = false;
            this.nightBuff = false;
        }

        public BattleModifier(boolean attackerFaithBuff,
                              boolean defenderFaithBuff,
                              int moraleBonus, int luckBonus,
                              boolean grandmasterBuff,
                              boolean medicBuff,
                              boolean nightBuff) {
            this.attackerFaithBuff = attackerFaithBuff;
            this.defenderFaithBuff = defenderFaithBuff;
            this.moraleBonus = moraleBonus;
            this.luckBonus = luckBonus;
            this.grandmasterBuff = grandmasterBuff;
            this.medicBuff = medicBuff;
            this.nightBuff = nightBuff;
        }

        private double getAttackerModifier() {
            return (1.0
                    * ((double) moraleBonus / 100)
                    * ((double) (100 + luckBonus) / 100)
                    + ((double) (grandmasterBuff ? 10 : 0) / 100))
                    * ((double) (attackerFaithBuff ? 100 : 50) / 100);
        }

        private double getDefenderModifier() {
            return 1.0
                    * ((double) (defenderFaithBuff ? 100 : 50) / 100)
                    * ((double) (100 + (nightBuff ? 100 : 0)) / 100)
                    * wall.getWallBonusModifier();
        }
    }

    @Getter
    public class BattleArmy extends Army {
        public BattleArmy() {
            super();
        }

        public BattleArmy(Integer spearmanNumber, Integer swordsmanNumber, Integer archerNumber, Integer heavyCavalryNumber, Integer axeFighterNumber, Integer lightCavalryNumber, Integer mountedArcherNumber, Integer ramNumber, Integer catapultNumber, Integer paladinNumber, Integer noblemanNumber, Integer berserkerNumber, Integer trebuchetNumber) {
            super(spearmanNumber, swordsmanNumber, archerNumber, heavyCavalryNumber, axeFighterNumber, lightCavalryNumber, mountedArcherNumber, ramNumber, catapultNumber, paladinNumber, noblemanNumber, berserkerNumber, trebuchetNumber);
        }

        public BattleArmy(Army army) {
            super(army);
        }

        public BattleArmy(Map<UnitType, Integer> units) {
            super(units);
        }

        public BattleArmy getPercentageOfArmy(double percentage) {
            var army = army();
            HashMap<UnitType, Integer> newArmy = EnumSet.allOf(UnitType.class).stream()
                    .collect(Collectors.toMap(t -> t, t -> (int) (army.get(t) * percentage), (o, n) -> n, HashMap::new));
            return new BattleArmy(newArmy);
        }

        public BattleArmy getInfArmy() {
            var army = army();
            HashMap<UnitType, Integer> newArmy = EnumSet.allOf(UnitType.class).stream()
                    .collect(Collectors.toMap(t -> t, t -> 0, (o, n) -> n, HashMap::new));
            army.entrySet().stream()
                    .filter(e -> e.getKey().isInfantry())
                    .forEach(e -> newArmy.put(e.getKey(), army.get(e.getKey())));
            return new BattleArmy(newArmy);
        }

        public BattleArmy getCavArmy() {
            var army = army();
            HashMap<UnitType, Integer> newArmy = EnumSet.allOf(UnitType.class).stream()
                    .collect(Collectors.toMap(t -> t, t -> 0, (o, n) -> n, HashMap::new));
            army.entrySet().stream()
                    .filter(e -> e.getKey().isCavalry())
                    .forEach(e -> newArmy.put(e.getKey(), army.get(e.getKey())));
            return new BattleArmy(newArmy);
        }

        public BattleArmy getArcArmy() {
            var army = army();
            HashMap<UnitType, Integer> newArmy = EnumSet.allOf(UnitType.class).stream()
                    .collect(Collectors.toMap(t -> t, t -> 0, (o, n) -> n, HashMap::new));
            army.entrySet().stream()
                    .filter(e -> e.getKey().isArchers())
                    .forEach(e -> newArmy.put(e.getKey(), army.get(e.getKey())));
            return new BattleArmy(newArmy);
        }

        // DEF
        public int getTotalInfDefPower() {
            return EnumSet.allOf(UnitType.class).stream()
                    .mapToInt(type -> get(type) * units.get(type).getInfantryDef())
                    .sum();
        }

        public Map<UnitType, Integer> getInfDefPower() {
            return EnumSet.allOf(UnitType.class).stream()
                    .collect(Collectors.toMap(t -> t, t -> get(t) * units.get(t).getInfantryDef(), (o, n) -> n, HashMap::new));
        }

        public Map<UnitType, Double> getInfPerDefPower() {
            int totalInfDefPower = getTotalInfDefPower();
            return getInfDefPower().entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> (double) entry.getValue() / totalInfDefPower
                    ));
        }

        public int getTotalCavDefPower() {
            return EnumSet.allOf(UnitType.class).stream()
                    .mapToInt(type -> get(type) * units.get(type).getCavalryDef())
                    .sum();
        }

        public Map<UnitType, Integer> getCavDefPower() {
            return EnumSet.allOf(UnitType.class).stream()
                    .collect(Collectors.toMap(t -> t, t -> get(t) * units.get(t).getCavalryDef(), (o, n) -> n, HashMap::new));
        }

        public Map<UnitType, Double> getCavPerDefPower() {
            int totalCavDefPower = getTotalCavDefPower();
            return getCavDefPower().entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> (double) entry.getValue() / totalCavDefPower
                    ));
        }

        public int getTotalArcDefPower() {
            return EnumSet.allOf(UnitType.class).stream()
                    .mapToInt(type -> get(type) * units.get(type).getArcherDef())
                    .sum();
        }

        public Map<UnitType, Integer> getArcDefPower() {
            return EnumSet.allOf(UnitType.class).stream()
                    .collect(Collectors.toMap(t -> t, t -> get(t) * units.get(t).getArcherDef(), (o, n) -> n, HashMap::new));
        }

        public Map<UnitType, Double> getArcPerDefPower() {
            int totalArcDefPower = getTotalArcDefPower();
            return getArcDefPower().entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> (double) entry.getValue() / totalArcDefPower
                    ));
        }

        // ATC
        public int getTotalAtcPower() {
            return EnumSet.allOf(UnitType.class).stream()
                    .mapToInt(type -> get(type) * units.get(type).getAttack())
                    .sum();
        }

        public Map<UnitType, Integer> getAtcPower() {
            return EnumSet.allOf(UnitType.class).stream()
                    .collect(Collectors.toMap(t -> t, t -> get(t) * units.get(t).getAttack(), (o, n) -> n, HashMap::new));
        }

        public Map<UnitType, Double> getPerAtcPower() {
            int totalAtcPower = getTotalAtcPower();
            Map<UnitType, Integer> atcPower = getAtcPower();
            return atcPower.entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> (double) entry.getValue() / totalAtcPower
                    ));
        }

        // SUBTRACT

        public void subtractBasedOnDefPower(double power) {
            Map<UnitType, Double> infPerAtcPower = getPerAtcPower();
            Map<UnitType, Integer> losses = infPerAtcPower.entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> (int) Math.round(power * entry.getValue() / (units.get(entry.getKey()).getAttack() * battleModifier.getAttackerModifier()))
                    ));
            this.subtract(Army.of(losses));
        }

        public void subtractBasedOnInfAtcPower(double power) {
            Map<UnitType, Double> infPerDefPower = getInfPerDefPower();
            Map<UnitType, Integer> losses = infPerDefPower.entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> (int) Math.round(power * entry.getValue() / (units.get(entry.getKey()).getInfantryDef() * battleModifier.getDefenderModifier()))
                    ));
            this.subtract(Army.of(losses));
        }

        public void subtractBasedOnCavAtcPower(double power) {
            Map<UnitType, Double> cavPerDefPower = getCavPerDefPower();
            Map<UnitType, Integer> losses = cavPerDefPower.entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> (int) Math.round(power * entry.getValue() / (units.get(entry.getKey()).getCavalryDef() * battleModifier.getDefenderModifier()))
                    ));
            this.subtract(Army.of(losses));
        }

        public void subtractBasedOnArcAtcPower(double power) {
            Map<UnitType, Double> arcPerDefPower = getArcPerDefPower();
            Map<UnitType, Integer> losses = arcPerDefPower.entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> (int) Math.round(power * entry.getValue() / (units.get(entry.getKey()).getArcherDef() * battleModifier.getDefenderModifier()))
                    ));
            this.subtract(Army.of(losses));
        }


        public int getArmyProvisions() {
            return EnumSet.allOf(UnitType.class).stream()
                    .mapToInt(type -> get(type) * units.get(type).getCost().getProvision())
                    .sum();
        }
    }

    @Getter
    @NoArgsConstructor
    public class Wall {
        private int wallDefenseBonus;
        private double wallBonusModifier; // 1 - default
        private int startWallLevel;
        private int resultingWallLevel;

        private double demolitionModifier;

        public Wall(int startWallLevel) {
            this.startWallLevel = startWallLevel;

        }

        public void init() {
            demolitionModifier = getDemolitionModifier();
            if (startWallLevel == 0) {
                resultingWallLevel = 0;
            } else {
                var def = getWallHitPoints(startWallLevel) * 2;
                var atc = attackerArmy.get(RAM) * battleModifier.getAttackerModifier();
                var wsp = (demolitionModifier * atc) / def;
                var newWall = startWallLevel - wsp < 10 ? Math.round(startWallLevel - wsp) : Math.ceil(startWallLevel - wsp);
                resultingWallLevel = newWall < 0 ? 0 : (int) newWall;
            }
            wallBonusModifier = 1 + resultingWallLevel * 0.05;
            wallDefenseBonus = resultingWallLevel == 0 ? 0 : (int) Math.round(Math.pow(1.25, resultingWallLevel) * 20);
        }

        public void finish() {
            if (resultingWallLevel == 0) {
                return;
            }
            var def = getWallHitPoints(resultingWallLevel) * 2;
            var atc = attackerArmy.get(RAM) * battleModifier.getAttackerModifier();
            var wsp = atc / def;
            var newWall = resultingWallLevel - wsp < 10 ? Math.round(resultingWallLevel - wsp) : Math.ceil(resultingWallLevel - wsp);
            resultingWallLevel = newWall < 0 ? 0 : (int) newWall;
        }

        private double getDemolitionModifier() {
            var wallBonus = startWallLevel == 0 ? 0 : Math.round(Math.pow(1.2515, (startWallLevel - 1)) * 20);
            var attackerProvisions = attackerArmy.getArmyProvisions() - attackerArmy.get(RAM) * units.get(RAM).getCost().getProvision();
            var defenderProvisions = wallBonus + defenderArmy.getArmyProvisions();
            return ((double) attackerProvisions / defenderProvisions > 1) ? 1 : ((double) attackerProvisions / defenderProvisions);
        }

        private Integer getWallHitPoints(Integer level) {
            return switch (level) {
                case 0 -> 0;
                case 1 -> 3;
                case 2 -> 3;
                case 3 -> 4;
                case 4 -> 4;
                case 5 -> 4;
                case 6 -> 5;
                case 7 -> 5;
                case 8 -> 6;
                case 9 -> 6;
                case 10 -> 7;
                case 11 -> 8;
                case 12 -> 9;
                case 13 -> 9;
                case 14 -> 10;
                case 15 -> 11;
                case 16 -> 13;
                case 17 -> 14;
                case 18 -> 15;
                case 19 -> 17;
                case 20 -> 18;
                default -> 0;
            };
        }
    }
}
