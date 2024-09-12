package pl.app.battle.application.domain.tower_attack;

import lombok.Getter;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.app.battle.application.domain.battle.Battle;
import pl.app.battle.application.domain.battle.BattleCharacter;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Getter
public class TowerAttack {
    private static final Logger logger = LoggerFactory.getLogger(TowerAttack.class);
    @Getter
    private TowerAttackLog log;
    private TowerAttackInfo info;
    private TowerAttaackCharacterManager characterManager;
    private TowerAttackBattleManager battleManager;
    private TowerAttackFinishManager finishManager;

    @Getter
    public class TowerAttackFinishManager {
        private TowerAttackResult result;

        public TowerAttackFinishManager() {
            this.result = new TowerAttackResult(info.getTowerAttackId(), info.getGodId(), characterManager.getTeam());
            this.result.setTowerAttackEnded(false);
        }

        private void setTeamWin() {
            log.send(new InnerTowerAttackEvent.AttackEndedEvent(true, battleManager.getCurrentNumberOfSeconds()));
            finishTowerAttack();
        }

        private void setTeamLost() {
            log.send(new InnerTowerAttackEvent.AttackEndedEvent(false, battleManager.getCurrentNumberOfSeconds()));
            finishTowerAttack();
        }

        private void setBattleEndedWithoutWinner() {
            log.send(new InnerTowerAttackEvent.AttackEndedEvent(null, battleManager.getCurrentNumberOfSeconds()));
            finishTowerAttack();
        }

        private void finishTowerAttack() {
            logger.debug("--ended tower attack--");
            this.result.setTowerAttackEnded(true);
            result.setBattleResults(battleManager.getBattles().stream().map(e -> e.getFinishManager().getBattleResult()).collect(Collectors.toSet()));
            result.setLog(log);
            result.setStart(info.getStart());
            result.setEnd(info.getStart().plus(battleManager.getCurrentNumberOfSeconds(), ChronoUnit.SECONDS));
            logLog();
        }
    }

    @Getter
    public class TowerAttackBattleManager {
        private Set<Battle> battles;
        private Integer maxNumberOfSeconds = 25;
        private Integer currentNumberOfSeconds = 0;

        public TowerAttackBattleManager() {
            this.battles = new LinkedHashSet<>();
        }
        /**
         * @return time of walk completion
         */
        public Instant teamWalk() {
            currentNumberOfSeconds += 1;
            log.send(new InnerTowerAttackEvent.TeamWalkedEvent(1));
            checkIfTowerAttackEnded();
            return info.getStart().plus(this.currentNumberOfSeconds,ChronoUnit.SECONDS);
        }

        private void checkIfTowerAttackEnded() {
            if (currentNumberOfSeconds >= maxNumberOfSeconds) {
                finishManager.setTeamWin();
            }
        }
        /**
         * @return time of battle completion
         */
        public Instant teamStartNewBattle() {
            Battle battle = new Battle(characterManager.getTeam(), characterManager.getMonstersForNewBattle());
            battles.add(battle);
            log.send(new InnerTowerAttackEvent.NewBattleStartedEvent(info.getTowerAttackId()));
            battle.startBattle();
            var numberOfSeconds = battle.getFinishManager().getBattleResult().getNumberOfRounds();
            var teamWin = battle.getFinishManager().getBattleResult().getIsTeam1Win();
            log.send(new InnerTowerAttackEvent.BattleEndedEvent(teamWin, numberOfSeconds));
            currentNumberOfSeconds += numberOfSeconds;
            if (teamWin) {
                checkIfTowerAttackEnded();
            } else {
                finishManager.setTeamLost();
            }
            return info.getStart().plus(this.currentNumberOfSeconds,ChronoUnit.SECONDS);
        }


    }

    @Getter
    public class TowerAttaackCharacterManager {
        private Set<BattleCharacter> team;
        private Set<BattleCharacter> monsterList;

        public TowerAttaackCharacterManager(Set<BattleCharacter> team, Set<BattleCharacter> monsterList) {
            this.team = team;
            this.monsterList = monsterList;
        }

        private Set<BattleCharacter> getMonstersForNewBattle() {
            monsterList.forEach(BattleCharacter::reset);
            return monsterList;
        }
    }

    @Getter
    public class TowerAttackInfo {
        private ObjectId towerAttackId;
        private ObjectId godId;
        private Instant start;

        public TowerAttackInfo(ObjectId godId) {
            this.towerAttackId = ObjectId.get();
            this.godId = godId;
            this.start = Instant.now();
        }
    }

    public TowerAttack(ObjectId godId, Set<BattleCharacter> team, Set<BattleCharacter> monsterList) {
        this.info = new TowerAttackInfo(godId);
        this.log = new TowerAttackLog();
        this.characterManager = new TowerAttaackCharacterManager(team, monsterList);
        this.battleManager = new TowerAttackBattleManager();
        this.finishManager = new TowerAttackFinishManager();
        start();
    }

    public void start() {
        logger.debug("--starting tower attack--");
        this.log.send(new InnerTowerAttackEvent.AttackStartedEvent(info.getTowerAttackId()));
    }

    private void logLog() {
        logger.debug("--log--");
        IntStream.range(0, log.getEvents().size())
                .boxed()
                .collect(Collectors.toMap(i -> i, log.getEvents()::get))
                .forEach((idx, event) -> logger.debug("{}:{}", idx, event));
    }


    static class TeamLost extends Exception {
        public TeamLost() {
            super("team lost");
        }
    }

    static class TowerAttackEnded extends Exception {
        public TowerAttackEnded() {
            super("tower attack ended");
        }
    }
}
