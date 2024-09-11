package pl.app.character.query.dto;

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
public class CharacterDto implements Serializable {
    private ObjectId id;
    private String name;
    private CharacterProfession profession;
    private CharacterRace race;
    private String imageId;
    private LevelDto level;
    private Statistics statistics;
}
