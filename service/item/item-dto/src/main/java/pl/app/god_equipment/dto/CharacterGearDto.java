package pl.app.god_equipment.dto;

import lombok.*;
import org.bson.types.ObjectId;
import pl.app.common.shared.model.Statistics;
import pl.app.gear.dto.GearDto;
import pl.app.item.query.dto.OutfitDto;
import pl.app.item.query.dto.WeaponDto;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CharacterGearDto extends GearDto implements Serializable{
    private ObjectId characterId;

    public CharacterGearDto(OutfitDto helmet, OutfitDto armor, OutfitDto gloves, OutfitDto boots, OutfitDto belt, OutfitDto ring, OutfitDto amulet, OutfitDto talisman, WeaponDto leftHand, WeaponDto rightHand, ObjectId characterId) {
        super(helmet, armor, gloves, boots, belt, ring, amulet, talisman, leftHand, rightHand);
        this.characterId = characterId;
    }
}