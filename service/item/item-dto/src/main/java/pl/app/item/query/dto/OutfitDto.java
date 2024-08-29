package pl.app.item.query.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import pl.app.common.shared.model.ItemType;
import pl.app.common.shared.model.Money;
import pl.app.common.shared.model.Statistics;

import java.io.Serializable;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OutfitDto extends ItemDto implements Serializable {
    protected Integer quality;
    protected Long persistence;
    protected Long durability;
    protected Long strength;
    protected Long speed;
    protected Long criticalRate;
    protected Long criticalDamage;
    protected Long accuracy;
    protected Long resistance;

    public OutfitDto(ObjectId id, ItemType type, ObjectId templateId, String name, String description, String imageId, Money money, Integer quality, Long persistence, Long durability, Long strength, Long speed, Long criticalRate, Long criticalDamage, Long accuracy, Long resistance) {
        super(id, type, templateId, name, description, imageId, money);
        this.quality = quality;
        this.persistence = persistence;
        this.durability = durability;
        this.strength = strength;
        this.speed = speed;
        this.criticalRate = criticalRate;
        this.criticalDamage = criticalDamage;
        this.accuracy = accuracy;
        this.resistance = resistance;
    }

    @JsonIgnore
    public Statistics getStatistic() {
        return new Statistics(
                this.persistence,
                this.durability,
                this.strength,
                this.speed,
                this.criticalRate,
                this.criticalDamage,
                this.accuracy,
                this.resistance
        );
    }
}
