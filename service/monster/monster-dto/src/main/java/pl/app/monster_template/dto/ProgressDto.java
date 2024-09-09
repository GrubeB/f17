package pl.app.monster_template.dto;

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
public class ProgressDto implements Serializable {
    private Long exp;
}
