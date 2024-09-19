package pl.app.tower_attack.application.domain;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import pl.app.battle.application.domain.CharacterResult;
import pl.app.unit.application.domain.BattleCharacter;
import pl.app.battle.application.domain.BattleResult;
import pl.app.unit.application.domain.BattleUnit;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;


@Getter
@Setter
@Document(collection = "attack_tower_results")
public class TowerAttackResult {
    @Id
    private ObjectId towerAttackId;
    private Boolean towerAttackEnded;
    private ObjectId godId;
    private Boolean isWin;
    private Set<ObjectId> characterIdsList;
    @DocumentReference
    private Set<BattleResult> battleResults;
    @DocumentReference
    private TowerAttackLog log;

    private Instant start;
    private Instant end;

    @SuppressWarnings("unused")
    public TowerAttackResult() {
    }

    public TowerAttackResult(ObjectId towerAttackId, ObjectId godId, Set<? extends BattleUnit> team) {
        this.towerAttackId = towerAttackId;
        this.godId = godId;
        this.characterIdsList = team.stream().map(BattleUnit::getUnitId).collect(Collectors.toSet());
    }

    public List<CharacterResult> getCharacterResults() {
        Map<ObjectId, List<CharacterResult>> results = this.battleResults.stream()
                .map(BattleResult::getCharacterResults)
                .flatMap(List::stream)
                .collect(Collectors.groupingBy(CharacterResult::getCharacterId));
        return results.values().stream()
                .map(characterResults -> characterResults.stream().reduce(CharacterResult::add))
                .filter(Optional::isPresent).map(Optional::get)
                .collect(Collectors.toList());
    }
}
