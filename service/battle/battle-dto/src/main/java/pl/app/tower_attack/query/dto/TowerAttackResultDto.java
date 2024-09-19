package pl.app.tower_attack.query.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import pl.app.battle.application.domain.CharacterResult;
import pl.app.battle.query.dto.BattleResultDto;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.Set;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class TowerAttackResultDto implements Serializable {
    private ObjectId towerAttackId;
    private Boolean towerAttackEnded;
    private ObjectId godId;
    private Boolean isWin;
    private Set<ObjectId> characterIdsList;
    private Set<BattleResultDto> battleResults;
    private List<CharacterResult> characterResults;
    private Instant start;
    private Instant end;
}
