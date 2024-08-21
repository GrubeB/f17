package pl.app.item_template.application.port.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.app.item_template.application.domain.ItemType;

import java.io.Serializable;


public interface ItemTemplateCommand {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class CreateItemTemplateCommand implements Serializable {
        private String type;
        private String name;
        private String description;
        private String imageId;
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class CreateOutfitTemplateCommand implements Serializable {
        private String type;
        private String name;
        private String description;
        private String imageId;

        private Long persistence;
        private Long durability;
        private Long strength;
        private Long speed;
        private Long criticalRate;
        private Long criticalDamage;
        private Long accuracy;
        private Long resistance;
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
        private Long durability;
        private Long strength;
        private Long speed;
        private Long criticalRate;
        private Long criticalDamage;
        private Long accuracy;
        private Long resistance;

        private Integer minDmg;
        private Integer maxDmg;
    }
}
