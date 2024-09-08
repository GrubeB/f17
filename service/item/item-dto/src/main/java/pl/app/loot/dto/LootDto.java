package pl.app.loot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import pl.app.common.shared.model.Money;
import pl.app.item_template.query.dto.ItemTemplateDto;
import pl.app.item_template.query.dto.OutfitTemplateDto;
import pl.app.item_template.query.dto.WeaponTemplateDto;
import pl.app.loot.aplication.domain.LootItem;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LootDto implements Serializable {
    private ObjectId id; // domainObjectId
    private Money money;
    private Set<LootItemDto> items;
    private Set<LootOutfitDto> outfits;
    private Set<LootWeaponDto> weapons;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LootItemDto implements Serializable {
        private ItemTemplateDto itemTemplate;
        private Integer chance;
        private Integer amount;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LootOutfitDto implements Serializable {
        private OutfitTemplateDto itemTemplate;
        private Integer chance;
        private Integer amount;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LootWeaponDto implements Serializable {
        private WeaponTemplateDto itemTemplate;
        private Integer chance;
        private Integer amount;
    }
}