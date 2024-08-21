package pl.app.item.application.domain;

import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.app.item_template.application.domain.OutfitTemplate;

@Document(collection = "outfits")
@Getter
public class Outfit extends Item {
    protected Integer generatedForLevel;

    @SuppressWarnings("unused")
    public Outfit() {
        super();
    }

    public Outfit(OutfitTemplate template, Integer generatedForLevel) {
        super(template);
        this.generatedForLevel = generatedForLevel;
    }

    /* GETTERS */
    @Override
    public OutfitTemplate getTemplate() {
        return (OutfitTemplate) super.getTemplate();
    }

    public Long getPersistence() {
        return getTemplate().getPersistence() + generatedForLevel * getTemplate().getPersistence() * getTemplate().getPersistencePercentage() / 100_000;
    }

    public Long getDurability() {
        return getTemplate().getDurability() + generatedForLevel * getTemplate().getDurability() * getTemplate().getDurabilityPercentage() / 100_000;
    }

    public Long getStrength() {
        return getTemplate().getStrength() + generatedForLevel * getTemplate().getStrength() * getTemplate().getStrengthPercentage() / 100_000;
    }

    public Long getSpeed() {
        return getTemplate().getSpeed() + generatedForLevel * getTemplate().getSpeed() * getTemplate().getSpeedPercentage() / 100_000;
    }

    public Long getCriticalRate() {
        return getTemplate().getCriticalRate() + generatedForLevel * getTemplate().getCriticalRate() * getTemplate().getCriticalRatePercentage() / 100_000;
    }

    public Long getCriticalDamage() {
        return getTemplate().getCriticalDamage() + generatedForLevel * getTemplate().getCriticalDamage() * getTemplate().getCriticalDamagePercentage() / 100_000;
    }

    public Long getAccuracy() {
        return getTemplate().getAccuracy() + generatedForLevel * getTemplate().getAccuracy() * getTemplate().getAccuracyPercentage() / 100_000;
    }

    public Long getResistance() {
        return getTemplate().getResistance() + generatedForLevel * getTemplate().getResistance() * getTemplate().getResistancePercentage() / 100_000;
    }


}
