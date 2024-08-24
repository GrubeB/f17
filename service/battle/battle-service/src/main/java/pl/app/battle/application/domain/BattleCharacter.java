package pl.app.battle.application.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.app.common.shared.model.Statistics;
import pl.app.item.query.dto.WeaponDto;

import java.util.Optional;
import java.util.Random;
import java.util.Set;

@Getter
public class BattleCharacter {
    private static final Logger logger = LoggerFactory.getLogger(BattleCharacter.class);
    private Short innerId;
    private Info info;

    private Statistics baseStatistics;
    private Statistics gearStatistics;
    private Statistics statistics;

    private TurnSpeed turnSpeed;
    private CharacterStatus status;
    private Actions actions;

    private Set<BattleCharacter> allies;
    private Set<BattleCharacter> enemies;
    private BattleLog log;

    public BattleCharacter(ObjectId id,
                           ObjectId godId,
                           BattleCharacterType type,
                           String profession,
                           String name,
                           Integer level,
                           Long exp,
                           Statistics base,
                           Statistics gear,
                           Statistics statistics,
                           Long hp,
                           Long def,
                           Long attackPower,
                           WeaponDto leftHand,
                           WeaponDto rightHand) {
        this.info = new Info(id, godId, type, profession, name, level, exp);
        this.baseStatistics = base;
        this.gearStatistics = gear;
        this.statistics = statistics;
        this.turnSpeed = new TurnSpeed(statistics.getSpeed());
        this.status = new CharacterStatus(hp, def);
        this.actions = new Actions(attackPower, statistics, leftHand, rightHand);
    }

    @Getter
    public class Info {
        private ObjectId id;
        private ObjectId godId;
        private BattleCharacterType type;
        private String profession;
        private String name;
        private Integer level;
        private Long exp;

        public Info(ObjectId id, ObjectId godId, BattleCharacterType type, String profession, String name, Integer level, Long exp) {
            this.id = id;
            this.godId = godId;
            this.type = type;
            this.profession = profession;
            this.name = name;
            this.level = level;
            this.exp = exp;
        }
    }

    public class TurnSpeed {
        private Long attackSpeed;
        private Long startingTurnSpeed;
        private Long turnCounter;

        public TurnSpeed(Long attackSpeed) {
            this.attackSpeed = attackSpeed;
            this.startingTurnSpeed = null;
            this.turnCounter = null;
        }

        public void updateTurnCounter() {
            final var currentTurnCounter = turnCounter;
            final var newTurnCounter = turnCounter - attackSpeed;
            logger.debug("{} updated turn counter from {} to {}", BattleCharacter.this, currentTurnCounter, newTurnCounter);
            turnCounter = newTurnCounter;
            log.send(new InnerBattleEvent.CharacterTurnCounterUpdatedEvent(innerId, currentTurnCounter, newTurnCounter));
        }

        public void updateTurnCounter(Long numberOfTimes) {
            final var currentTurnCounter = turnCounter;
            final var newTurnCounter = turnCounter - numberOfTimes * attackSpeed;
            logger.debug("{} updated turn counter from {} to {}", BattleCharacter.this, currentTurnCounter, newTurnCounter);
            log.send(new InnerBattleEvent.CharacterTurnCounterUpdatedEvent(innerId, currentTurnCounter, newTurnCounter));
            turnCounter = newTurnCounter;
        }

        public void setTurnCounterToStartingPosition() {
            final var currentTurnCounter = turnCounter;
            final var newTurnCounter = startingTurnSpeed;
            logger.debug("{} updated turn counter from {} to {}", BattleCharacter.this, currentTurnCounter, newTurnCounter);
            log.send(new InnerBattleEvent.CharacterTurnCounterUpdatedEvent(innerId, currentTurnCounter, newTurnCounter));
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
    public class CharacterStatus {
        private Long maxHp;
        private Long currentHp;
        private Long maxDef;
        private Long currentDef;
        private Boolean isDead;

        public CharacterStatus(Long maxHp, Long maxDef) {
            this.maxHp = maxHp;
            this.maxDef = maxDef;
            this.currentHp = maxHp;
            this.currentDef = maxDef;
            this.isDead = false;
        }

        private void subtractHp(Long damage) {
            currentHp = currentHp - damage;
            logger.debug("\t\t{} took {} dmg", BattleCharacter.this, damage);
            log.send(new InnerBattleEvent.CharacterHpLostEvent(innerId, damage));
            if (currentHp <= 0) {
                die();
            }
        }

        private void die() {
            logger.debug("\t\t{} died", this);
            isDead = true;
            log.send(new InnerBattleEvent.CharacterDiedEvent(innerId));
            turnSpeed.setTurnCounterToStartingPosition();
        }

        public boolean isDead() {
            return isDead;
        }
    }

    public class Actions {
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
        private final Random random = new Random();

        public Actions(Long attackPower, Statistics statistics, WeaponDto leftHand, WeaponDto rightHand) {
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
        }

        public void baseAttack() {
            Optional<BattleCharacter> enemyToAttack = getRandomEnemyToAttack();
            enemyToAttack.ifPresent(this::baseAttack);
        }

        public void baseAttack(BattleCharacter enemy) {
            var dmg = getValueBetween(getMinBaseDmg(), getMaxBaseDmg());
            Hit hit = new Hit(dmg, currentCriticalRate, currentCriticalDamage, currentAccuracy);
            logger.debug("\t\t{} atacking {} with {}", BattleCharacter.this, enemy, hit);
            log.send(new InnerBattleEvent.CharacterAttackedEvent(innerId, enemy.getInnerId()));
            enemy.getActions().takeHit(hit);
        }

        private void takeHit(Hit hit) {
            logger.debug("\t\t{} take hit", BattleCharacter.this);
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

        private Optional<BattleCharacter> getRandomEnemyToAttack() {
            return enemies.stream().filter(ch -> !ch.getStatus().isDead()).findFirst();
        }

        private Long getMaxBaseDmg() {
            long maxBaseDmg = 0;
            maxBaseDmg += calculateMaxDmgForHand(leftHand);
            maxBaseDmg += calculateMaxDmgForHand(rightHand);
            return maxBaseDmg;
        }

        private Long getMinBaseDmg() {
            long minBaseDmg = 0;
            minBaseDmg += calculateMinDmgForHand(leftHand);
            minBaseDmg += calculateMinDmgForHand(rightHand);
            return minBaseDmg;
        }

        private long calculateMinDmgForHand(WeaponDto hand) {
            if (hand == null) {
                return this.currentAttackPower * info.getLevel() / 3;
            } else {
                return this.currentAttackPower * hand.getMinDmg();
            }
        }

        private long calculateMaxDmgForHand(WeaponDto hand) {
            if (hand == null) {
                return this.currentAttackPower * info.getLevel() / 3;
            } else {
                return this.currentAttackPower * hand.getMaxDmg();
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

    /* TURN METHODS */
    public void startTurn() {
        logger.debug("\t\t{} starting his turn", this);
        log.send(new InnerBattleEvent.CharacterTurnStartedEvent(innerId));
        actions.baseAttack();
        endTurn();
        logger.debug("\t\t{} ended his turn", this);
    }

    public void endTurn() {
        logger.debug("\t\t{} turn ended", this);
        turnSpeed.setTurnCounterToStartingPosition();
    }

    public boolean isCharacterTurn() {
        return turnSpeed.checkIfCounterIsZero();
    }

    /* INIT METHODS */

    public void setTeams(Set<BattleCharacter> allies, Set<BattleCharacter> enemies) {
        this.allies = allies;
        this.enemies = enemies;
    }

    public void setBattleLog(BattleLog log) {
        this.log = log;
    }

    public void setInnerId(Short innerId) {
        this.innerId = innerId;
    }

    @Override
    public String toString() {
        return info.getName() + "(" + getStatus().getCurrentHp() + "/" + getStatus().getMaxHp() + ")";
    }
}
