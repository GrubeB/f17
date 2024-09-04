package pl.app.battle.query.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.io.Serializable;
import java.time.Instant;
import java.util.Set;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class TowerAttackResultDto implements Serializable {
    private ObjectId towerAttackId;
    private ObjectId godId;
    private Boolean isWin;
    private Set<ObjectId> characterIdsList;
    private Set<BattleResultDto> battleResults;
    private Instant start;
    private Instant end;
}
