package pl.app.monster_gear.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import pl.app.gear.dto.GearDto;
import pl.app.item.query.dto.OutfitDto;
import pl.app.item.query.dto.WeaponDto;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MonsterGearDto extends GearDto implements Serializable{
    private ObjectId monsterId;

    public MonsterGearDto(OutfitDto helmet, OutfitDto armor, OutfitDto gloves, OutfitDto boots, OutfitDto belt, OutfitDto ring, OutfitDto amulet, OutfitDto talisman, WeaponDto leftHand, WeaponDto rightHand, ObjectId monsterId) {
        super(helmet, armor, gloves, boots, belt, ring, amulet, talisman, leftHand, rightHand);
        this.monsterId = monsterId;
    }
}