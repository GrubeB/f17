package pl.app.item_template.application.port.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


public interface ItemTemplateCommand {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class CreateOutfitTemplateCommand implements Serializable {
        private String type;
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
    class CreateWeaponTemplateCommand implements Serializable {
        private String type;
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
}
