package pl.app.character.query.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import pl.app.common.shared.model.Statistics;
import pl.app.god_equipment.dto.CharacterGearDto;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CharacterWithGearDto implements Serializable {
    private ObjectId id;
    private String name;
    private String profession;

    private LevelDto level;

    private Statistics base;
    private Statistics gear;
    private Statistics statistics;

    private Long hp;
    private Long def;
    private Long attackPower;

    private CharacterGearDto characterGearDto;
}
