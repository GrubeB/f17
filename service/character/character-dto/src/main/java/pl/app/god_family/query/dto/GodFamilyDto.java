package pl.app.god_family.query.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import pl.app.character.query.dto.CharacterDto;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GodFamilyDto implements Serializable {
    private ObjectId id;
    private ObjectId godId;
    private List<CharacterDto> characters;
}
