package pl.app.battle.application.domain.tower_attack;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import pl.app.battle.application.domain.battle.BattleCharacter;
import pl.app.battle.application.domain.battle.BattleResult;

import java.time.Instant;
import java.util.Set;
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
    public TowerAttackResult(ObjectId towerAttackId, ObjectId godId, Set<BattleCharacter> team) {
        this.towerAttackId = towerAttackId;
        this.godId = godId;
        this.characterIdsList = team.stream().map(ch -> ch.getInfo().getId()).collect(Collectors.toSet());
    }
}
