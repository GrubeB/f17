package pl.app.family.query.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import pl.app.character.query.dto.CharacterDto;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FamilyDto implements Serializable {
    private ObjectId godId;
    private List<CharacterDto> characters;
}
