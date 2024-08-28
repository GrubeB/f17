package pl.app.item.query.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import pl.app.common.shared.model.ItemType;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WeaponDto extends OutfitDto implements Serializable {
    protected Long minDmg;
    protected Long maxDmg;


    public WeaponDto(ObjectId id, ItemType type, ObjectId templateId, String name, String description, String imageId, Long persistence, Long durability, Long strength, Long speed, Long criticalRate, Long criticalDamage, Long accuracy, Long resistance, Long minDmg, Long maxDmg) {
        super(id, type, templateId, name, description, imageId, persistence, durability, strength, speed, criticalRate, criticalDamage, accuracy, resistance);
        this.minDmg = minDmg;
        this.maxDmg = maxDmg;
    }
}
