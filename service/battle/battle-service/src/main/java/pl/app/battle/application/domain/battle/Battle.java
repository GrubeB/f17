package pl.app.battle.application.domain.battle;

import lombok.Getter;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Getter
public class Battle {
    private static final Logger logger = LoggerFactory.getLogger(Battle.class);
    private BattleInfo info;
    private BattleTeamsManager teamsManager;
    private BattleTurnManager turnManager;
    private BattleLog log;
    private BattleFinishManager finishManager;

    @Getter
    public class BattleInfo {
        private ObjectId battleId;
        private Instant startTime;

        public BattleInfo() {
            this.battleId = ObjectId.get();
            this.startTime = Instant.now();
        }
    }

    @Getter
    public class BattleTurnManager {
        private Long startingTurnSpeed;
        private Integer maxNumberOfTurns = 20_000;
        private Integer currentNumberOfTurns = 0;

        public BattleTurnManager() {
            calculateStartingTurnSpeed();
        }

        private void calculateStartingTurnSpeed() {
            Set<Long> characterSpeeds = Stream.of(teamsManager.getTeam1(), teamsManager.getTeam2())
                    .flatMap(Set::stream)
                    .map(character -> character.getStatistics().getSpeed())
                    .collect(Collectors.toSet());
            this.startingTurnSpeed = characterSpeeds.stream()
                    .reduce(1L, (a, b) -> a * b);
        }

        public void incrementNumberOfTurns() {
            this.currentNumberOfTurns += 1;
        }

        private void updateAttackSpeedOfTeams() {
            Long maxTimesTurnCounterCanByUpdated = Stream.of(teamsManager.getTeam1(), teamsManager.getTeam2())
                    .flatMap(Set::stream)
                    .map(character -> character.getTurnManager().getHowManyTimeTurnCounterCanByUpdated())
                    .min(Long::compareTo)
                    .orElse(1L);
            for (var character : teamsManager.getTeam1()) {
                if (character.getStatus().isDead()) continue;
                character.getTurnManager().updateTurnCounter(maxTimesTurnCounterCanByUpdated);
            }
            for (var character : teamsManager.getTeam2()) {
                if (character.getStatus().isDead()) continue;
                character.getTurnManager().updateTurnCounter(maxTimesTurnCounterCanByUpdated);
            }
        }

        private Boolean isTeam1LastTurn = true;
        private Short lastTurnCharacterInnerId = null;

        private void startTeam1Turn() throws TeamLost {
            for (var character : teamsManager.getTeam1()) {
                if (character.getStatus().isDead()) continue;
                if (character.getTurnManager().isCharacterTurn()) {
                    character.getTurnManager().startTurn();
                    turnManager.incrementNumberOfTurns();
                    isTeam1LastTurn = true;
                    lastTurnCharacterInnerId = character.getInfo().getInnerId();
                    if (isTeamLost(teamsManager.getTeam2())) {
                        throw new TeamLost(false);
                    }
                }
            }
        }

        private void startTeam2Turn() throws TeamLost {
            for (var character : teamsManager.getTeam2()) {
                if (character.getStatus().isDead()) continue;
                if (character.getTurnManager().isCharacterTurn()) {
                    character.getTurnManager().startTurn();
                    turnManager.incrementNumberOfTurns();
                    isTeam1LastTurn = true;
                    lastTurnCharacterInnerId = character.getInfo().getInnerId();
                    if (isTeamLost(teamsManager.getTeam1())) {
                        throw new TeamLost(true);
                    }
                }
            }
        }

        private boolean isTeamLost(Set<BattleCharacter> team) {
            return team.stream().allMatch(ch -> ch.getStatus().isDead());
        }

        private void verifyIfBattleEnd() throws BattleEnded {
            if (currentNumberOfTurns >= maxNumberOfTurns) {
                throw new BattleEnded();
            }
        }
    }

    @Getter
    public class BattleTeamsManager {
        private Set<BattleCharacter> team1;
        private Set<BattleCharacter> team2;

        public BattleTeamsManager(Set<BattleCharacter> team1, Set<BattleCharacter> team2) {
            this.team1 = team1;
            this.team2 = team2;
        }
    }

    @Getter
    public class BattleFinishManager {
        private BattleResult battleResult;

        public BattleFinishManager() {
            this.battleResult = new BattleResult(info.getBattleId(), teamsManager.getTeam1(), teamsManager.getTeam2());
        }

        private void setTeam1Win() {
            battleResult.setIsTeam1Win(true);
            log.send(new InnerBattleEvent.BattleEndedEvent(true));
            battleResult.setProgress(calculateExpForWinningTeam(teamsManager.getTeam1(), teamsManager.getTeam2()));
            battleResult.setMoney(calculateMoneyForWinningTeam(teamsManager.getTeam1(), teamsManager.getTeam2()));
        }

        private void setTeam2Win() {
            battleResult.setIsTeam1Win(false);
            log.send(new InnerBattleEvent.BattleEndedEvent(false));
            battleResult.setProgress(calculateExpForWinningTeam(teamsManager.getTeam2(),teamsManager.getTeam1()));
            battleResult.setMoney(calculateMoneyForWinningTeam(teamsManager.getTeam2(), teamsManager.getTeam1()));
        }

        private void setBattleEndedWithoutWinner() {
            battleResult.setIsTeam1Win(null);
            log.send(new InnerBattleEvent.BattleEndedEvent(null));
        }

        private void finishBattle() {
            battleResult.setNumberOfRounds(turnManager.getCurrentNumberOfTurns());
            battleResult.setStart(info.getStartTime());
            battleResult.setLog(log);
        }

        // TODO exp and money should be calculated based on members in team1 and team2
        private Map<ObjectId, Long> calculateExpForWinningTeam(Set<BattleCharacter> team1, Set<BattleCharacter> team2) {
            return team1.stream().collect(Collectors.toMap(ch -> ch.getInfo().getId(), ch -> 100L));
        }

        private Map<ObjectId, Long> calculateMoneyForWinningTeam(Set<BattleCharacter> team1, Set<BattleCharacter> team2) {
            return team1.stream().collect(Collectors.toMap(ch -> ch.getInfo().getId(), ch -> 10_000L));
        }
    }

    public Battle(Set<BattleCharacter> team1, Set<BattleCharacter> team2) {
        this.info = new BattleInfo();
        this.teamsManager = new BattleTeamsManager(team1, team2);
        this.turnManager = new BattleTurnManager();
        this.log = new BattleLog();
        this.finishManager = new BattleFinishManager();
        initTeams();
    }

    public void startBattle() {
        logger.debug("--starting battle--");
        try {
            while (true) {
                turnManager.updateAttackSpeedOfTeams();
                turnManager.startTeam1Turn();
                turnManager.startTeam2Turn();
                turnManager.verifyIfBattleEnd();
            }
        } catch (TeamLost e) {
            if (e.isTeam1Lost()) {
                finishManager.setTeam2Win();
            } else {
                finishManager.setTeam1Win();
            }
        } catch (BattleEnded ended) {
            finishManager.setBattleEndedWithoutWinner();
        }
        logger.debug("--ended battle--");
        finishManager.finishBattle();
        logLog();
    }
    private void initTeams() {
        IdGenerator idGen = new IdGenerator();
        for (var character : teamsManager.getTeam1()) {
            character.getTurnManager().setStartingTurnSpeed(turnManager.getStartingTurnSpeed());
            character.setTeams(teamsManager.getTeam1(), teamsManager.getTeam2());
            character.setBattleLog(log);
            character.getInfo().setInnerId(idGen.getNextId());
        }
        for (var character : teamsManager.getTeam2()) {
            character.getTurnManager().setStartingTurnSpeed(turnManager.getStartingTurnSpeed());
            character.setTeams(teamsManager.getTeam2(), teamsManager.getTeam1());
            character.setBattleLog(log);
            character.getInfo().setInnerId(idGen.getNextId());
        }
    }
    private void logLog() {
        logger.debug("--battle log--");
        IntStream.range(0, log.getEvents().size())
                .boxed()
                .collect(Collectors.toMap(i -> i, log.getEvents()::get))
                .forEach((idx, event) -> logger.debug("{}:{}", idx, event));
    }

    /* HELP CLASS */

    @Getter
    static class TeamLost extends Exception {
        private boolean team1Lost;

        public TeamLost(boolean team1Lost) {
            super("battle ended");
            this.team1Lost = team1Lost;
        }
    }

    static class BattleEnded extends Exception {
        public BattleEnded() {
            super("battle ended");
        }
    }

    static class IdGenerator {
        private short nextId;

        public short getNextId() {
            var id = nextId;
            nextId++;
            return id;
        }
    }
}
