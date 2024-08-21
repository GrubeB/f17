package pl.app.item_template.query.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeaponTemplateDto extends OutfitTemplateDto implements Serializable {
    protected Integer minDmg;
    protected Integer maxDmg;

    public WeaponTemplateDto(ObjectId id, String type, String name, String description, String imageId, Long persistence, Long durability, Long strength, Long speed, Long criticalRate, Long criticalDamage, Long accuracy, Long resistance, Integer minDmg, Integer maxDmg) {
        super(id, type, name, description, imageId, persistence, durability, strength, speed, criticalRate, criticalDamage, accuracy, resistance);
        this.minDmg = minDmg;
        this.maxDmg = maxDmg;
    }
}
