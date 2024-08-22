package pl.app.account_equipment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import pl.app.item.query.dto.OutfitDto;
import pl.app.item.query.dto.WeaponDto;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountEquipmentDto implements Serializable {
    private ObjectId id;
    private ObjectId accountId;
    private List<OutfitDto> outfits;
    private List<WeaponDto> weapons;

    private List<CharacterGearDto> characterGears;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CharacterGearDto implements Serializable {
        private ObjectId characterId;

        private OutfitDto helmet;
        private OutfitDto armor;
        private OutfitDto gloves;
        private OutfitDto boots;
        private OutfitDto belt;
        private OutfitDto ring;
        private OutfitDto amulet;
        private OutfitDto talisman;

        private WeaponDto leftHand;
        private WeaponDto rightHand;
    }
}
