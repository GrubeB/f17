package pl.app.item.query.dto;

import lombok.*;
import org.bson.types.ObjectId;

import java.io.Serializable;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OutfitDto extends ItemDto implements Serializable {
    protected Long persistence;
    protected Long durability;
    protected Long strength;
    protected Long speed;
    protected Long criticalRate;
    protected Long criticalDamage;
    protected Long accuracy;
    protected Long resistance;

    public OutfitDto(ObjectId id, String type, ObjectId templateId, String name, String description, String imageId, Long persistence, Long durability, Long strength, Long speed, Long criticalRate, Long criticalDamage, Long accuracy, Long resistance) {
        super(id, type, templateId, name, description, imageId);
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
