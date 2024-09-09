package pl.app.equipment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import pl.app.gear.dto.GearDto;
import pl.app.item.query.dto.OutfitDto;
import pl.app.item.query.dto.WeaponDto;

import java.io.Serializable;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EquipmentDto implements Serializable {
    private ObjectId godId;
    private Set<OutfitDto> outfits;
    private Set<WeaponDto> weapons;
    private Set<GearDto> characterGears;
}
