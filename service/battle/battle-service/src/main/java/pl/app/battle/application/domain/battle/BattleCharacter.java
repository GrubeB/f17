package pl.app.battle.application.domain.battle;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.app.common.shared.model.CharacterProfession;
import pl.app.common.shared.model.Statistics;
import pl.app.item.query.dto.WeaponDto;
import pl.app.monster.query.dto.MonsterWithGearDto;

import java.util.Optional;
import java.util.Random;
import java.util.Set;

@Getter
public class BattleCharacter {
    private static final Logger logger = LoggerFactory.getLogger(BattleCharacter.class);
    private BattleCharacterInfo info;
    private BattleCharacterStatus status;

    private Statistics baseStatistics;
    private Statistics gearStatistics;
    private Statistics statistics;

    private BattleCharacterTurnManager turnManager;
    private BattleCharacterActions actions;

    private Set<BattleCharacter> allies;
    private Set<BattleCharacter> enemies;
    private BattleLog log;

    public BattleCharacter(ObjectId id, ObjectId godId, BattleCharacterType type, CharacterProfession profession,
                           String name, Integer level, Long exp,
                           Statistics base, Statistics gear, Statistics statistics,
                           Long hp, Long def, Long attackPower, WeaponDto leftHand, WeaponDto rightHand) {
        this.info = new BattleCharacterInfo(id, godId, type, profession, name, level, exp);
        this.baseStatistics = base;
        this.gearStatistics = gear;
        this.statistics = statistics;
        this.turnManager = new BattleCharacterTurnManager(statistics.getSpeed());
        this.status = new BattleCharacterStatus(hp, def);
        this.actions = new BattleCharacterActions(attackPower, statistics, leftHand, rightHand);
    }

    public BattleCharacter(MonsterWithGearDto dto) {
        this(null, null, BattleCharacterType.MONSTER, dto.getProfession(),
                dto.getName(), dto.getLevel(), 0L,
                dto.getBase(), dto.getGear(), dto.getStatistics(),
                dto.getHp(), dto.getDef(), dto.getAttackPower(), dto.getMonsterGear().getLeftHand(), dto.getMonsterGear().getLeftHand());
    }

    @Getter
    public class BattleCharacterInfo {
        private Short innerId;
        private ObjectId id;
        private ObjectId godId;
        private BattleCharacterType type;
        private CharacterProfession profession;
        private String name;
        private Integer level;
        private Long exp;

        public BattleCharacterInfo(ObjectId id, ObjectId godId, BattleCharacterType type, CharacterProfession profession, String name, Integer level, Long exp) {
            this.id = id;
            this.godId = godId;
            this.type = type;
            this.profession = profession;
            this.name = name;
            this.level = level;
            this.exp = exp;
        }

        public void setInnerId(Short innerId) {
            this.innerId = innerId;
        }
    }

    @Getter
    public class BattleCharacterTurnManager {
        private Long attackSpeed;
        private Long startingTurnSpeed;
        private Long turnCounter;

        public BattleCharacterTurnManager(Long attackSpeed) {
            this.attackSpeed = attackSpeed;
            this.startingTurnSpeed = null;
            this.turnCounter = null;
        }

        public void startTurn() {
            logger.debug("\t\t{} starting his turn", BattleCharacter.this);
            log.send(new InnerBattleEvent.CharacterTurnStartedEvent(info.getInnerId()));
            actions.makeAction();
            endTurn();
        }

        public void endTurn() {
            turnManager.setTurnCounterToStartingPosition();
            logger.debug("\t\t{} turn ended", BattleCharacter.this);
        }

        public boolean isCharacterTurn() {
            return turnManager.checkIfCounterIsZero();
        }

        public void updateTurnCounter() {
            final var currentTurnCounter = turnCounter;
            final var newTurnCounter = turnCounter - attackSpeed;
            logger.debug("{} updated turn counter from {} to {}", BattleCharacter.this, currentTurnCounter, newTurnCounter);
            turnCounter = newTurnCounter;
            log.send(new InnerBattleEvent.CharacterTurnCounterUpdatedEvent(info.getInnerId(), currentTurnCounter, newTurnCounter));
        }

        public void updateTurnCounter(Long numberOfTimes) {
            final var currentTurnCounter = turnCounter;
            final var newTurnCounter = turnCounter - numberOfTimes * attackSpeed;
            logger.debug("{} updated turn counter from {} to {}", BattleCharacter.this, currentTurnCounter, newTurnCounter);
            log.send(new InnerBattleEvent.CharacterTurnCounterUpdatedEvent(info.getInnerId(), currentTurnCounter, newTurnCounter));
            turnCounter = newTurnCounter;
        }

        public void setTurnCounterToStartingPosition() {
            final var currentTurnCounter = turnCounter;
            final var newTurnCounter = startingTurnSpeed;
            logger.debug("{} updated turn counter from {} to {}", BattleCharacter.this, currentTurnCounter, newTurnCounter);
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
    public class BattleCharacterStatus {
        private Long maxHp;
        private Long currentHp;
        private Long maxDef;
        private Long currentDef;
        private Boolean isDead;

        public BattleCharacterStatus(Long maxHp, Long maxDef) {
            this.maxHp = maxHp;
            this.maxDef = maxDef;
            reset();
        }

        private void subtractHp(Long damage) {
            currentHp = currentHp - damage;
            logger.debug("\t\t{} took {} dmg", BattleCharacter.this, damage);
            log.send(new InnerBattleEvent.CharacterHpLostEvent(info.getInnerId(), damage));
            if (currentHp <= 0) {
                die();
            }
        }

        private void die() {
            logger.debug("\t\t{} died", this);
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
        private WeaponDto leftHand;
        private WeaponDto rightHand;
        private final Random random = new Random();

        public BattleCharacterActions(Long attackPower, Statistics statistics, WeaponDto leftHand, WeaponDto rightHand) {
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

        public void makeAction() {
            this.baseAttack(); // TODO
        }

        public void baseAttack() {
            Optional<BattleCharacter> enemyToAttack = getRandomEnemyToAttack();
            enemyToAttack.ifPresent(this::baseAttack);
        }

        public void baseAttack(BattleCharacter enemy) {
            var dmg = getValueBetween(getMinBaseDmg(), getMaxBaseDmg());
            Hit hit = new Hit(dmg, currentCriticalRate, currentCriticalDamage, currentAccuracy);
            logger.debug("\t\t{} atacking {} with {}", BattleCharacter.this, enemy, hit);
            log.send(new InnerBattleEvent.CharacterAttackedEvent(info.getInnerId(), enemy.getInfo().getInnerId()));
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


    public void setTeams(Set<BattleCharacter> allies, Set<BattleCharacter> enemies) {
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
