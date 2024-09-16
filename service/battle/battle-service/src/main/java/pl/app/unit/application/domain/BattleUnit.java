package pl.app.unit.application.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.app.battle.application.domain.BattleLog;
import pl.app.battle.application.domain.InnerBattleEvent;
import pl.app.common.shared.model.CharacterProfession;
import pl.app.common.shared.model.Statistics;
import pl.app.item.query.dto.WeaponDto;

import java.util.Optional;
import java.util.Random;
import java.util.Set;

@Getter
public class BattleUnit {
    private static final Logger logger = LoggerFactory.getLogger(BattleUnit.class);
    private ObjectId unitId;
    private BattleUnitType type;
    private BattleUnitInfo info;
    private BattleUnitStatus status;
    private Statistics statistics;

    private BattleUnitTurnManager turnManager;
    private BattleUnitActions actions;

    private Set<? extends BattleUnit> allies;
    private Set<? extends BattleUnit> enemies;
    private BattleLog log;

    public BattleUnit(ObjectId unitId, BattleUnitType type, CharacterProfession profession, String name, Integer level, Long exp,
                      Statistics statistics,
                      Long hp, Long def, Long attackPower, WeaponDto leftHand, WeaponDto rightHand) {
        this.unitId = unitId;
        this.type = type;
        this.info = new BattleUnitInfo(profession, name, level, exp);
        this.statistics = statistics;
        this.turnManager = new BattleUnitTurnManager(statistics.getSpeed());
        this.status = new BattleUnitStatus(hp, def, attackPower, statistics, leftHand, rightHand);
        this.actions = new BattleUnitActions();
    }

    @Getter
    public class BattleUnitInfo {
        @Setter
        private Short innerId;
        private CharacterProfession profession;
        private String name;
        private Integer level;
        private Long exp;

        public BattleUnitInfo(CharacterProfession profession, String name, Integer level, Long exp) {
            this.profession = profession;
            this.name = name;
            this.level = level;
            this.exp = exp;
        }
    }

    @Getter
    public class BattleUnitTurnManager {
        private Long attackSpeed;
        private Long startingTurnSpeed;
        private Long turnCounter;

        public BattleUnitTurnManager(Long attackSpeed) {
            this.attackSpeed = attackSpeed;
            this.startingTurnSpeed = null;
            this.turnCounter = null;
        }

        public void startTurn() {
            logger.debug("\t\t{} starting his turn", BattleUnit.this);
            log.send(new InnerBattleEvent.CharacterTurnStartedEvent(info.getInnerId()));
            actions.makeAction();
            endTurn();
        }

        public void endTurn() {
            turnManager.setTurnCounterToStartingPosition();
            logger.debug("\t\t{} turn ended", BattleUnit.this);
        }

        public boolean isUnitTurn() {
            return turnManager.checkIfCounterIsZero();
        }

        public void updateTurnCounter() {
            final var currentTurnCounter = turnCounter;
            final var newTurnCounter = turnCounter - attackSpeed;
            logger.debug("{} updated turn counter from {} to {}", BattleUnit.this, currentTurnCounter, newTurnCounter);
            turnCounter = newTurnCounter;
            log.send(new InnerBattleEvent.CharacterTurnCounterUpdatedEvent(info.getInnerId(), currentTurnCounter, newTurnCounter));
        }

        public void updateTurnCounter(Long numberOfTimes) {
            final var currentTurnCounter = turnCounter;
            final var newTurnCounter = turnCounter - numberOfTimes * attackSpeed;
            logger.debug("{} updated turn counter from {} to {}", BattleUnit.this, currentTurnCounter, newTurnCounter);
            log.send(new InnerBattleEvent.CharacterTurnCounterUpdatedEvent(info.getInnerId(), currentTurnCounter, newTurnCounter));
            turnCounter = newTurnCounter;
        }

        public void setTurnCounterToStartingPosition() {
            final var currentTurnCounter = turnCounter;
            final var newTurnCounter = startingTurnSpeed;
            logger.debug("{} updated turn counter from {} to {}", BattleUnit.this, currentTurnCounter, newTurnCounter);
            log.send(new InnerBattleEvent.CharacterTurnCounterUpdatedEvent(info.getInnerId(), currentTurnCounter, newTurnCounter));
            turnCounter = startingTurnSpeed;
        }

        public boolean checkIfCounterIsZero() {
            if (turnCounter < 0) {
                logger.error("\t\tturnCounter is below 0");
            }
            return turnCounter <= 0;
        }

        public void setStartingTurnSpeed(Long startingTurnSpeed) {
            this.startingTurnSpeed = startingTurnSpeed;
            this.turnCounter = startingTurnSpeed;
        }

        public Long getHowManyTimeTurnCounterCanByUpdated() {
            return turnCounter / attackSpeed;
        }

    }

    @Getter
    public class BattleUnitStatus {
        private Long maxHp;
        private Long currentHp;
        private Long maxDef;
        private Long currentDef;

        private Long maxAttackPower;
        private Long currentAttackPower;
        private Long maxCriticalRate;
        private Long currentCriticalRate;
        private Long maxCriticalDamage;
        private Long currentCriticalDamage;
        private Long maxAccuracy;
        private Long currentAccuracy;
        private Long maxResistance;
        private Long currentResistance;
        private WeaponDto leftHand;
        private WeaponDto rightHand;

        private Boolean isDead;

        public BattleUnitStatus(Long maxHp, Long maxDef, Long attackPower, Statistics statistics, WeaponDto leftHand, WeaponDto rightHand) {
            this.maxHp = maxHp;
            this.maxDef = maxDef;

            this.maxAttackPower = attackPower;
            this.maxCriticalRate = statistics.getCriticalRate();
            this.maxCriticalDamage = statistics.getCriticalDamage();
            this.maxAccuracy = statistics.getAccuracy();
            this.maxResistance = statistics.getResistance();

            this.currentAttackPower = maxAttackPower;
            this.currentCriticalRate = maxCriticalRate;
            this.currentCriticalDamage = maxCriticalDamage;
            this.currentAccuracy = maxAccuracy;
            this.currentResistance = maxResistance;

            this.leftHand = leftHand;
            this.rightHand = rightHand;

            reset();
        }

        private void subtractHp(Long damage) {
            currentHp = currentHp - damage;
            logger.debug("\t\t{} took {} dmg", BattleUnit.this, damage);
            log.send(new InnerBattleEvent.CharacterHpLostEvent(info.getInnerId(), damage));
            if (currentHp <= 0) {
                die();
            }
        }

        private void die() {
            logger.debug("\t\t{} died", BattleUnit.this);
            isDead = true;
            log.send(new InnerBattleEvent.CharacterDiedEvent(info.getInnerId()));
            turnManager.setTurnCounterToStartingPosition();
        }

        private void reset() {
            this.currentHp = maxHp;
            this.currentDef = maxDef;
            this.isDead = false;
        }

        public boolean isDead() {
            return isDead;
        }
    }

    public class BattleUnitActions {
        private final Random random = new Random();

        public BattleUnitActions() {
        }

        public void makeAction() {
            this.baseAttack(); // TODO
        }

        public void baseAttack() {
            Optional<? extends BattleUnit> enemyToAttack = getRandomEnemyToAttack();
            enemyToAttack.ifPresent(this::baseAttack);
        }

        public void baseAttack(BattleUnit enemy) {
            var dmg = getValueBetween(getMinBaseDmg(), getMaxBaseDmg());
            Hit hit = new Hit(dmg, status.getCurrentCriticalRate(), status.getCurrentCriticalDamage(), status.getCurrentAccuracy());
            logger.debug("\t\t{} atacking {} with {}", BattleUnit.this, enemy, hit);
            log.send(new InnerBattleEvent.CharacterAttackedEvent(info.getInnerId(), enemy.getInfo().getInnerId()));
            enemy.getActions().takeHit(hit);
        }

        private void takeHit(Hit hit) {
            logger.debug("\t\t{} take hit", BattleUnit.this);
            var isCritical = isCriticalAttack(hit.getCriticalRate());
            var dmg = isCritical ? getCriticalDmg(hit.getDmg(), hit.getCriticalDamage()) : hit.getDmg();
            status.subtractHp(dmg);
        }


        /* HELP METHODS */
        private long getCriticalDmg(long baseDmg, long criticalDmg) {
            return baseDmg + Math.round(((double) criticalDmg / (double) 100_000) * baseDmg);
        }

        private boolean isCriticalAttack(Long criticalRate) {
            return random.nextFloat() <= (float) criticalRate / 100_000;
        }

        private Optional<? extends BattleUnit> getRandomEnemyToAttack() {
            return enemies.stream().filter(ch -> !ch.getStatus().isDead()).findFirst();
        }

        private Long getMaxBaseDmg() {
            long maxBaseDmg = 0;
            maxBaseDmg += calculateMaxDmgForHand(status.getLeftHand());
            maxBaseDmg += calculateMaxDmgForHand(status.getRightHand());
            return maxBaseDmg;
        }

        private Long getMinBaseDmg() {
            long minBaseDmg = 0;
            minBaseDmg += calculateMinDmgForHand(status.getLeftHand());
            minBaseDmg += calculateMinDmgForHand(status.getRightHand());
            return minBaseDmg;
        }

        private long calculateMinDmgForHand(WeaponDto hand) {
            if (hand == null) {
                return status.getCurrentAttackPower() * info.getLevel() / 3;
            } else {
                return status.getCurrentAttackPower() * hand.getMinDmg();
            }
        }

        private long calculateMaxDmgForHand(WeaponDto hand) {
            if (hand == null) {
                return status.getCurrentAttackPower() * info.getLevel() / 3;
            } else {
                return status.getCurrentAttackPower() * hand.getMaxDmg();
            }
        }

        private long getValueBetween(Long minBaseDmg, Long maxBaseDmg) {
            return random.nextLong(maxBaseDmg - minBaseDmg + 1) + minBaseDmg;
        }

        @Data
        @AllArgsConstructor
        public static class Hit {
            private Long dmg;
            private Long criticalRate;
            private Long criticalDamage;
            private Long accuracy;
        }
    }


    public void setTeams(Set<? extends BattleUnit> allies, Set<? extends BattleUnit> enemies) {
        this.allies = allies;
        this.enemies = enemies;
    }

    public void setBattleLog(BattleLog log) {
        this.log = log;
    }

    public void reset() {
        this.status.reset();
    }

    @Override
    public String toString() {
        return info.getName() + "(" + getStatus().getCurrentHp() + "/" + getStatus().getMaxHp() + ")";
    }
}
