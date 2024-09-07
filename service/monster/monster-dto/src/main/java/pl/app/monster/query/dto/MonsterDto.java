package pl.app.monster.query.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import pl.app.common.shared.model.CharacterProfession;
import pl.app.common.shared.model.CharacterRace;
import pl.app.common.shared.model.Statistics;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonsterDto implements Serializable {
    private ObjectId id;
    private String name;
    private String description;
    private CharacterRace race;
    private CharacterProfession profession;
    private String imageId;

    private Statistics statistics;
    private Integer level;
}
