package pl.app.battle.application.domain;

import lombok.Getter;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TowerAttack {
    private static final Logger logger = LoggerFactory.getLogger(TowerAttack.class);
    private ObjectId towerAttackId;
    private ObjectId godId;
    private Set<BattleCharacter> team;
    private Set<BattleCharacter> monsterList;
    @Getter
    private TowerAttackLog log;
    private Set<Battle> battles;
    @Getter
    private TowerAttackResult result;
    private Integer maxNumberOfSeconds = 6000;
    private Integer currentNumberOfSeconds = 0;

    private Instant start;

    public TowerAttack(ObjectId godId, Set<BattleCharacter> team, Set<BattleCharacter> monsterList) {
        this.towerAttackId = ObjectId.get();
        this.godId = godId;
        this.log = new TowerAttackLog();
        this.battles = new LinkedHashSet<>();
        this.team = team;
        this.monsterList = monsterList;
        this.result = new TowerAttackResult(this.towerAttackId,this.godId,  this.team);
        this.start = Instant.now();
    }

    public void start() {
        logger.debug("--starting tower attack--");
        this.log.send(new InnerTowerAttackEvent.AttackStartedEvent(this.towerAttackId));
        try {
            while (true) {
                teamWalk();
                verifyIfAttackEnd();
                startNewBattle();
            }
        } catch (TowerAttackEnded ended) {
            this.log.send(new InnerTowerAttackEvent.AttackEndedEvent(true, this.currentNumberOfSeconds));
            this.result.setIsWin(true);
        } catch (TeamLost e) {
            this.log.send(new InnerTowerAttackEvent.AttackEndedEvent(false, this.currentNumberOfSeconds));
            this.result.setIsWin(false);
        } finally {
            logger.debug("--ended tower attack--");
        }
        finishTowerAttack();
        logLog();
    }


    private void finishTowerAttack() {
        this.result.setBattleResults(this.battles.stream().map(Battle::getBattleResult).collect(Collectors.toSet()));
        this.result.setLog(this.log);
        this.result.setStart(this.start);
        this.result.setEnd(this.start.plus(this.currentNumberOfSeconds, ChronoUnit.SECONDS));
    }

    private void logLog() {
        logger.debug("--log--");
        IntStream.range(0, log.getEvents().size())
                .boxed()
                .collect(Collectors.toMap(i -> i, log.getEvents()::get))
                .forEach((idx, event) -> logger.debug("{}:{}", idx, event));
    }

    private void startNewBattle() throws TeamLost {
        Battle battle = new Battle(team, getMonstersForNewBattle());
        this.battles.add(battle);
        this.log.send(new InnerTowerAttackEvent.NewBattleStartedEvent(battle.getBattleId()));
        battle.setStart(this.start.plus(this.currentNumberOfSeconds, ChronoUnit.SECONDS));
        battle.startBattle();
        var numberOfSeconds = battle.getBattleResult().getNumberOfRounds();
        var teamWin = battle.getBattleResult().getIsTeam1Win();
        this.log.send(new InnerTowerAttackEvent.BattleEndedEvent(teamWin, numberOfSeconds));
        this.currentNumberOfSeconds += numberOfSeconds;
        if (!teamWin) {
            throw new TeamLost();
        }
    }

    private Set<BattleCharacter> getMonstersForNewBattle() {
        this.monsterList.forEach(BattleCharacter::reset);
        return this.monsterList;
    }

    private void teamWalk() {
        this.currentNumberOfSeconds += 100;
        this.log.send(new InnerTowerAttackEvent.TeamWalkedEvent(100));
    }

    private void verifyIfAttackEnd() throws TowerAttackEnded {
        if (currentNumberOfSeconds >= maxNumberOfSeconds) {
            throw new TowerAttackEnded();
        }
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
