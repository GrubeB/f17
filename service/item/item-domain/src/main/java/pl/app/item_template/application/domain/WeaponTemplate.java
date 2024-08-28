package pl.app.item_template.application.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "weapon_templates")
@Getter
@Setter
public class WeaponTemplate extends OutfitTemplate {
    private Long minDmg;
    private Long minDmgPercentage;
    private Long maxDmg;
    private Long maxDmgPercentage;

    @SuppressWarnings("unused")
    public WeaponTemplate() {
    }

    public WeaponTemplate(String typeName, String name, String description, String imageId, Long persistence, Long persistencePercentage, Long durability, Long durabilityPercentage, Long strength, Long strengthPercentage, Long speed, Long speedPercentage, Long criticalRate, Long criticalRatePercentage, Long criticalDamage, Long criticalDamagePercentage, Long accuracy, Long accuracyPercentage, Long resistance, Long resistancePercentage, Long minDmg, Long minDmgPercentage, Long maxDmg, Long maxDmgPercentage) {
        super(typeName, name, description, imageId, persistence, persistencePercentage, durability, durabilityPercentage, strength, strengthPercentage, speed, speedPercentage, criticalRate, criticalRatePercentage, criticalDamage, criticalDamagePercentage, accuracy, accuracyPercentage, resistance, resistancePercentage);
        this.minDmg = minDmg;
        this.minDmgPercentage = minDmgPercentage;
        this.maxDmg = maxDmg;
        this.maxDmgPercentage = maxDmgPercentage;
    }
}
