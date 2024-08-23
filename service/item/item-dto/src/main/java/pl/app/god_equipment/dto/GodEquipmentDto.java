package pl.app.god_equipment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import pl.app.item.query.dto.OutfitDto;
import pl.app.item.query.dto.WeaponDto;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GodEquipmentDto implements Serializable {
    private ObjectId id;
    private ObjectId godId;
    private Set<OutfitDto> outfits;
    private Set<WeaponDto> weapons;
    private Set<CharacterGearDto> characterGears;
}
