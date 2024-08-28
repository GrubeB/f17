package pl.app.item_template.application.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "outfit_templates")
@Getter
@Setter
public class OutfitTemplate extends ItemTemplate {
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

    @SuppressWarnings("unused")
    public OutfitTemplate() {
    }

    public OutfitTemplate(String typeName, String name, String description, String imageId, Long persistence, Long durability, Long strength, Long speed, Long criticalRate, Long criticalDamage, Long accuracy, Long resistance) {
        super(typeName, name, description, imageId);
        this.persistence = persistence;
        this.durability = durability;
        this.strength = strength;
        this.speed = speed;
        this.criticalRate = criticalRate;
        this.criticalDamage = criticalDamage;
        this.accuracy = accuracy;
        this.resistance = resistance;
    }

    public OutfitTemplate(String typeName, String name, String description, String imageId, Long persistence, Long persistencePercentage, Long durability, Long durabilityPercentage, Long strength, Long strengthPercentage, Long speed, Long speedPercentage, Long criticalRate, Long criticalRatePercentage, Long criticalDamage, Long criticalDamagePercentage, Long accuracy, Long accuracyPercentage, Long resistance, Long resistancePercentage) {
        super(typeName, name, description, imageId);
        this.persistence = persistence;
        this.persistencePercentage = persistencePercentage;
        this.durability = durability;
        this.durabilityPercentage = durabilityPercentage;
        this.strength = strength;
        this.strengthPercentage = strengthPercentage;
        this.speed = speed;
        this.speedPercentage = speedPercentage;
        this.criticalRate = criticalRate;
        this.criticalRatePercentage = criticalRatePercentage;
        this.criticalDamage = criticalDamage;
        this.criticalDamagePercentage = criticalDamagePercentage;
        this.accuracy = accuracy;
        this.accuracyPercentage = accuracyPercentage;
        this.resistance = resistance;
        this.resistancePercentage = resistancePercentage;
    }
}
