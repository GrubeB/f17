package pl.app.tower.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import pl.app.common.shared.model.CharacterProfession;
import pl.app.common.shared.model.CharacterRace;
import pl.app.common.shared.model.Statistics;
import pl.app.monster.query.dto.MonsterDto;
import pl.app.monster.query.dto.MonsterWithGearDto;

import java.io.Serializable;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TowerLevelDto implements Serializable {
    private Integer level;
    private Set<MonsterWithGearDto> monsters;
    private Integer minNumberOfMonstersInBattle;
    private Integer maxNumberOfMonstersInBattle;
    private Integer energyCost;
}
