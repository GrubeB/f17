package pl.app.character.query.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import pl.app.common.shared.model.Statistics;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CharacterDto implements Serializable {
    private ObjectId id;
    private String name;
    private String profession;
    private LevelDto level;
    private Statistics statistics;

}
