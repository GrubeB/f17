package pl.app.item_template.query.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OutfitTemplateDto extends ItemTemplateDto implements Serializable {
    protected Long persistence;
    protected Long durability;
    protected Long strength;
    protected Long speed;
    protected Long criticalRate;
    protected Long criticalDamage;
    protected Long accuracy;
    protected Long resistance;

    public OutfitTemplateDto(ObjectId id, String type, String name, String description, String imageId, Long persistence, Long durability, Long strength, Long speed, Long criticalRate, Long criticalDamage, Long accuracy, Long resistance) {
        super(id, type, name, description, imageId);
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
