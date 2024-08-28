package pl.app.item_template.application.port.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import pl.app.common.shared.model.ItemType;

import java.io.Serializable;


public interface ItemTemplateCommand {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class CreateOutfitTemplateCommand implements Serializable {
        private ItemType type;
        private String name;
        private String description;
        private String imageId;

        private Long persistence;
        private Long persistencePercentage;
        private Long durability;
        private Long durabilityPercentage;
        private Long strength;
        private Long strengthPercentage;
        private Long speed;
        private Long speedPercentage;
        private Long criticalRate;
        private Long criticalRatePercentage;
        private Long criticalDamage;
        private Long criticalDamagePercentage;
        private Long accuracy;
        private Long accuracyPercentage;
        private Long resistance;
        private Long resistancePercentage;
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class UpdateOutfitTemplateCommand implements Serializable {
        private ObjectId id;
        private ItemType type;
        private String name;
        private String description;
        private String imageId;

        private Long persistence;
        private Long persistencePercentage;
        private Long durability;
        private Long durabilityPercentage;
        private Long strength;
        private Long strengthPercentage;
        private Long speed;
        private Long speedPercentage;
        private Long criticalRate;
        private Long criticalRatePercentage;
        private Long criticalDamage;
        private Long criticalDamagePercentage;
        private Long accuracy;
        private Long accuracyPercentage;
        private Long resistance;
        private Long resistancePercentage;
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class DeleteOutfitTemplateCommand implements Serializable {
        private ObjectId id;
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class CreateWeaponTemplateCommand implements Serializable {
        private ItemType type;
        private String name;
        private String description;
        private String imageId;

        private Long persistence;
        private Long persistencePercentage;
        private Long durability;
        private Long durabilityPercentage;
        private Long strength;
        private Long strengthPercentage;
        private Long speed;
        private Long speedPercentage;
        private Long criticalRate;
        private Long criticalRatePercentage;
        private Long criticalDamage;
        private Long criticalDamagePercentage;
        private Long accuracy;
        private Long accuracyPercentage;
        private Long resistance;
        private Long resistancePercentage;

        private Long minDmg;
        private Long minDmgPercentage;
        private Long maxDmg;
        private Long maxDmgPercentage;
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class UpdateWeaponTemplateCommand implements Serializable {
        private ObjectId id;
        private ItemType type;
        private String name;
        private String description;
        private String imageId;

        private Long persistence;
        private Long persistencePercentage;
        private Long durability;
        private Long durabilityPercentage;
        private Long strength;
        private Long strengthPercentage;
        private Long speed;
        private Long speedPercentage;
        private Long criticalRate;
        private Long criticalRatePercentage;
        private Long criticalDamage;
        private Long criticalDamagePercentage;
        private Long accuracy;
        private Long accuracyPercentage;
        private Long resistance;
        private Long resistancePercentage;

        private Long minDmg;
        private Long minDmgPercentage;
        private Long maxDmg;
        private Long maxDmgPercentage;
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class DeleteWeaponTemplateCommand implements Serializable {
        private ObjectId id;
    }
}
