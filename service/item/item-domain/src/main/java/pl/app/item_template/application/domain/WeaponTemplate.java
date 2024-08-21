package pl.app.item_template.application.domain;

import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "weapon_templates")
@Getter
public class WeaponTemplate extends OutfitTemplate {
    private Integer minDmg;
    private Integer maxDmg;
    @SuppressWarnings("unused")
    public WeaponTemplate() {
    }
    public WeaponTemplate(String typeName, String name, String description, String imageId, Long persistence, Long durability, Long strength, Long speed, Long criticalRate, Long criticalDamage, Long accuracy, Long resistance, Integer minDmg, Integer maxDmg) {
        super(typeName, name, description, imageId, persistence, durability, strength, speed, criticalRate, criticalDamage, accuracy, resistance);
        this.minDmg = minDmg;
        this.maxDmg = maxDmg;
    }
}
