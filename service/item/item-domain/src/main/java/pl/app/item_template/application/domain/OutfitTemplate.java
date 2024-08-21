package pl.app.item_template.application.domain;

import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "outfit_templates")
@Getter
public class OutfitTemplate extends ItemTemplate {
    private Long persistence;
    private Long durability;
    private Long strength;
    private Long speed;
    private Long criticalRate;
    private Long criticalDamage;
    private Long accuracy;
    private Long resistance;
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
}
