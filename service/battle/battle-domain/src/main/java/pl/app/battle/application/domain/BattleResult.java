package pl.app.battle.application.domain;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import pl.app.common.shared.model.Money;
import pl.app.unit.application.domain.BattleCharacter;
import pl.app.unit.application.domain.BattleMonster;
import pl.app.unit.application.domain.BattleUnit;
import pl.app.unit.application.domain.BattleUnitType;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Getter
@Document(collection = "battle_results")
public class BattleResult {
    @Id
    private ObjectId battleId;
    @Setter
    private Boolean isTeam1Win;
    private List<TeamUnit> team1;
    private List<TeamUnit> team2;
    private List<CharacterResult> characterResults;
    @DocumentReference
    @Setter
    private BattleLog log;
    @Setter
    private Integer numberOfRounds;
    private Instant start;
    private Instant end;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeamUnit {
        private ObjectId id;
        private BattleUnitType type;
    }

    @SuppressWarnings("unused")
    public BattleResult() {
    }

    public BattleResult(ObjectId battleId,  Set<? extends BattleUnit> team1,  Set<? extends BattleUnit> team2) {
        this.battleId = battleId;
        this.team1 = mapToTeamUnits(team1);
        this.team2 = mapToTeamUnits(team2);
        this.characterResults = Stream.of(team1, team2).flatMap(Set::stream)
                .filter(e -> e instanceof BattleCharacter).map(e->(BattleCharacter) e)
                .map(character -> new CharacterResult(character.getUnitId(), character.getGodId()))
                .collect(Collectors.toList());
    }
    private List<TeamUnit> mapToTeamUnits(Set<? extends BattleUnit> team){
        return team.stream()
                .map(e ->{
                    if(e instanceof BattleMonster battleMonster){
                        return new TeamUnit(battleMonster.getUnitId(), BattleUnitType.MONSTER);
                    } else if (e instanceof BattleCharacter battleCharacter){
                        return new TeamUnit(battleCharacter.getUnitId(), BattleUnitType.CHARACTER);
                    }
                    return null;
                }).toList();
    }
    public void setProgress(Map<ObjectId, Long> map) {
        map.forEach((id, exp) -> {
            getCharacterResultById(id).ifPresent(chr -> chr.setExp(exp));
        });
    }
    public void setLoot(Map<ObjectId, CharacterResult.Loot> map) {
        map.forEach((id, loot) -> {
            getCharacterResultById(id).ifPresent(chr -> chr.setLoot(loot));
        });
    }
    private Optional<CharacterResult> getCharacterResultById(ObjectId objectId) {
        return characterResults.stream().filter(ch -> ch.getCharacterId().equals(objectId)).findAny();
    }

    public void setStart(Instant start) {
        this.start = start;
        this.end = start.plus(this.numberOfRounds, ChronoUnit.SECONDS);
    }
}
