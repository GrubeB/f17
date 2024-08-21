package pl.app.battle.application.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.Random;
import java.util.Set;

@Getter
public class BattleCharacter {
    private static final Logger logger = LoggerFactory.getLogger(BattleCharacter.class);

    private ObjectId id;
    private BattleCharacterType type;
    private String profession;
    private String name;
    private Short innerId;

    private BattleCharacterStatistics statistics;
    private BattleCharacterTurnSpeed turnSpeed;
    private BattleCharacterStatus status;
    private BattleCharacterActions actions;

    private Set<BattleCharacter> allies;
    private Set<BattleCharacter> enemies;
    private BattleLog log;

    public BattleCharacter(ObjectId id, BattleCharacterType type, String profession,  String name, BattleCharacterStatistics statistics) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.statistics = statistics;
        this.status = new BattleCharacterStatus(profession, statistics);
        this.actions = new BattleCharacterActions(profession, statistics);
        this.turnSpeed = new BattleCharacterTurnSpeed(statistics.getSpeed());
    }


    public class BattleCharacterTurnSpeed {
        private Long attackSpeed;
        private Long startingTurnSpeed;
        private Long turnCounter;

        public BattleCharacterTurnSpeed(Long attackSpeed) {
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
    public class BattleCharacterStatus {
        private Long maxHp;
        private Long currentHp;
        private Long maxDef;
        private Long currentDef;
        private Boolean isDead;

        public BattleCharacterStatus(String profession, BattleCharacterStatistics statistics) {
            switch (profession) {
                case "MARKSMAN" -> {
                    this.maxHp = statistics.getPersistence() * 40L;
                    this.maxDef = statistics.getDurability();
                }
                case "WARRIOR" -> {
                    this.maxHp = statistics.getPersistence() * 50L;
                    this.maxDef = statistics.getDurability() * 2L;
                }
                case "MAGE" -> {
                    this.maxHp = statistics.getPersistence() * 35L;
                    this.maxDef = statistics.getDurability();
                }
                default -> throw new IllegalStateException("Unexpected value: " + profession);
            }
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

    public class BattleCharacterActions {
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
        private Long maxBaseDmg;
        private Long minBaseDmg;

        private final Random random = new Random();

        public BattleCharacterActions(String profession, BattleCharacterStatistics statistics) {
            switch (profession) {
                case "MARKSMAN" -> {
                    this.maxAttackPower = (long) (statistics.getStrength() * 3.5);
                }
                case "WARRIOR" -> {
                    this.maxAttackPower = (long) (statistics.getStrength() * 2.0);
                }
                case "MAGE" -> {
                    this.maxAttackPower = (long) (statistics.getStrength() * 4.0);
                }
                default -> throw new IllegalStateException("Unexpected value: " + profession);
            }
            this.currentAttackPower = maxAttackPower;
            this.maxCriticalRate = statistics.getCriticalRate();
            this.currentCriticalRate = maxCriticalRate;
            this.maxCriticalDamage = statistics.getCriticalDamage();
            this.currentCriticalDamage = maxCriticalDamage;
            this.maxAccuracy = statistics.getAccuracy();
            this.currentAccuracy = maxAccuracy;
            this.maxResistance = statistics.getResistance();
            this.currentResistance = maxResistance;
            //TODO get values from item
            this.maxBaseDmg = this.maxAttackPower * 7;
            this.minBaseDmg = this.maxAttackPower * 5;
        }

        public void baseAttack() {
            Optional<BattleCharacter> enemyToAttack = getRandomEnemyToAttack();
            enemyToAttack.ifPresent(this::baseAttack);
        }

        public void baseAttack(BattleCharacter enemy) {
            var dmg = getValueBetween(this.minBaseDmg, this.maxBaseDmg);
            Hit hit = new Hit(dmg, currentCriticalRate, currentCriticalDamage, currentAccuracy);
            logger.debug("\t\t{} atacking {} with {}", BattleCharacter.this, enemy, hit);
            log.send(new InnerBattleEvent.CharacterAttackedEvent(innerId, enemy.getInnerId()));
            enemy.getActions().takeHit(hit);
        }

        private void takeHit(Hit hit) {
            logger.debug("\t\t{} take hit",  BattleCharacter.this);
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
        return name + "(" + getStatus().getCurrentHp() + "/" + getStatus().getMaxHp() + ")";
    }
}
