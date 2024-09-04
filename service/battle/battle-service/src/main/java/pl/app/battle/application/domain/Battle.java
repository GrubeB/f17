package pl.app.battle.application.domain;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Battle {
    private static final Logger logger = LoggerFactory.getLogger(Battle.class);
    @Getter
    private ObjectId battleId;
    private Long startingTurnSpeed;
    private Set<BattleCharacter> team1;
    private Set<BattleCharacter> team2;
    private BattleLog log;
    @Getter
    private BattleResult battleResult;
    private Integer maxNumberOfRounds = 20_000;
    private Integer currentNumberOfRounds = 0;
    @Setter
    private Instant start;

    public Battle(Set<BattleCharacter> team1, Set<BattleCharacter> team2) {
        this.battleId = ObjectId.get();
        this.log = new BattleLog();
        this.team1 = team1;
        this.team2 = team2;
        this.battleResult = new BattleResult(battleId, team1, team2);
        calculateStartingTurnSpeed();
        initTeams();
        this.start = Instant.now();
    }

    private void calculateStartingTurnSpeed() {
        Set<Long> characterSpeeds = Stream.of(team1, team2)
                .flatMap(Set::stream)
                .map(character -> character.getStatistics().getSpeed())
                .collect(Collectors.toSet());
        this.startingTurnSpeed = characterSpeeds.stream()
                .reduce(1L, (a, b) -> a * b);
    }

    private void initTeams() {
        IdGenerator idGen = new IdGenerator();
        for (var character : team1) {
            character.getTurnSpeed().setStartingTurnSpeed(startingTurnSpeed);
            character.setTeams(team1, team2);
            character.setBattleLog(log);
            character.setInnerId(idGen.getNextId());
        }
        for (var character : team2) {
            character.getTurnSpeed().setStartingTurnSpeed(startingTurnSpeed);
            character.setTeams(team2, team1);
            character.setBattleLog(log);
            character.setInnerId(idGen.getNextId());
        }
    }

    public void startBattle() {
        logger.debug("--starting battle--");
        try {
            while (true) {
                logger.debug("--starting round {}--", currentNumberOfRounds);
                currentNumberOfRounds = currentNumberOfRounds + 1;
                updateAttackSpeedOfTeams();
                team1Turn();
                team2Turn();
                verifyIfBattleEnd();
            }
        } catch (BattleEnded ended) {
            logger.debug("--ended battle--");
        }
        finishBattle();
        logger.debug("--battle log--");
        IntStream.range(0, log.getEvents().size())
                .boxed()
                .collect(Collectors.toMap(i -> i, log.getEvents()::get))
                .forEach((idx, event) -> logger.debug("{}:{}", idx, event));
    }

    private void verifyIfBattleEnd() throws BattleEnded {
        if (currentNumberOfRounds >= maxNumberOfRounds) {
            throw new BattleEnded();
        }
    }

    private void finishBattle() {
        if (isTeamLost(team2)) {
            battleResult.setIsTeam1Win(true);
            log.send(new InnerBattleEvent.BattleEndedEvent(true));
            battleResult.setProgress(calculateExpForWinningTeam(team1, team2));
            battleResult.setMoney(calculateMoneyForWinningTeam(team1, team2));
        } else if (isTeamLost(team1)) {
            battleResult.setIsTeam1Win(false);
            log.send(new InnerBattleEvent.BattleEndedEvent(false));
            battleResult.setProgress(calculateExpForWinningTeam(team2, team1));
            battleResult.setMoney(calculateMoneyForWinningTeam(team2, team1));
        } else {
            battleResult.setIsTeam1Win(null);
            log.send(new InnerBattleEvent.BattleEndedEvent(null));
        }
        battleResult.setNumberOfRounds(currentNumberOfRounds);
        battleResult.setStart(this.start);
        battleResult.setLog(log);
    }


    // TODO exp and money should be calculated based on members in team1 and team2
    private Map<ObjectId, Long> calculateExpForWinningTeam(Set<BattleCharacter> team1, Set<BattleCharacter> team2) {
        return team1.stream().collect(Collectors.toMap(ch -> ch.getInfo().getId(), ch -> 100L));
    }

    private Map<ObjectId, Long> calculateMoneyForWinningTeam(Set<BattleCharacter> team1, Set<BattleCharacter> team2) {
        return team1.stream().collect(Collectors.toMap(ch -> ch.getInfo().getId(), ch -> 10_000L));
    }

    /* ATTACK  */
    private Boolean isTeam1LastTurn = true;
    private Short lastTurnCharacterId = null;

    private void team1Turn() throws BattleEnded {
        BattleCharacter lastTurnCharacter = teamTurn(team1, team2);
        if (Objects.nonNull(lastTurnCharacter)) {
            isTeam1LastTurn = true;
            lastTurnCharacterId = lastTurnCharacter.getInnerId();
        }
    }

    private void team2Turn() throws BattleEnded {
        BattleCharacter lastTurnCharacter = teamTurn(team2, team1);
        if (Objects.nonNull(lastTurnCharacter)) {
            isTeam1LastTurn = true;
            lastTurnCharacterId = lastTurnCharacter.getInnerId();
        }
    }

    private BattleCharacter teamTurn(Set<BattleCharacter> team, Set<BattleCharacter> enemies) throws BattleEnded {
        BattleCharacter lastTurnCharacter = null;
        for (var character : team) {
            if (character.getStatus().isDead()) continue;
            if (character.isCharacterTurn()) {
                character.startTurn();
                lastTurnCharacter = character;
                if (isTeamLost(enemies)) {
                    throw new BattleEnded();
                }
            }
        }
        return lastTurnCharacter;
    }


    /* ATTACK SPEED */
    private void updateAttackSpeedOfTeams() {
        Long maxTimesTurnCounterCanByUpdated = Stream.of(team1, team2)
                .flatMap(Set::stream)
                .map(character -> character.getTurnSpeed().getHowManyTimeTurnCounterCanByUpdated())
                .min(Long::compareTo)
                .orElse(1L);
        for (var character : team1) {
            if (character.getStatus().isDead()) continue;
            character.getTurnSpeed().updateTurnCounter(maxTimesTurnCounterCanByUpdated);
        }
        for (var character : team2) {
            if (character.getStatus().isDead()) continue;
            character.getTurnSpeed().updateTurnCounter(maxTimesTurnCounterCanByUpdated);
        }
    }

    /* END BATTLE */
    private boolean isTeamLost(Set<BattleCharacter> team) {
        return team.stream().allMatch(ch -> ch.getStatus().isDead());
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
