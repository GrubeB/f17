package pl.app.item.application.domain;

import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.app.item_template.application.domain.OutfitTemplate;

import java.util.Random;

@Getter
@Document(collection = "items")
public class Outfit extends Item {
    protected Integer generatedForLevel;
    protected Integer quality; // 75% - 125%
    private static final Integer minQuality = 75_000;
    private static final Integer maxQuality = 125_000;

    @SuppressWarnings("unused")
    public Outfit() {
        super();
    }

    public Outfit(OutfitTemplate template, Integer generatedForLevel) {
        super(template);
        this.generatedForLevel = generatedForLevel;
        this.quality = generateQuality();
    }

    private Integer generateQuality() {
        return new Random().nextInt(maxQuality - minQuality + 1) + minQuality;
    }

    /* GETTERS */
    @Override
    public OutfitTemplate getTemplate() {
        return (OutfitTemplate) super.getTemplate();
    }

    public Long getPersistence() {
        return getQuality()
                * (getTemplate().getPersistence() + getTemplate().getPersistence() * generatedForLevel * getTemplate().getPersistencePercentage() / 100_000)
                / 100_000;
    }

    public Long getDurability() {
        return getQuality()
                * (getTemplate().getDurability() + generatedForLevel * getTemplate().getDurability() * getTemplate().getDurabilityPercentage() / 100_000)
                / 100_000;
    }

    public Long getStrength() {
        return getQuality()
                * (getTemplate().getStrength() + generatedForLevel * getTemplate().getStrength() * getTemplate().getStrengthPercentage() / 100_000)
                / 100_000;
    }

    public Long getSpeed() {
        return getQuality()
                * (getTemplate().getSpeed() + generatedForLevel * getTemplate().getSpeed() * getTemplate().getSpeedPercentage() / 100_000)
                / 100_000;
    }

    public Long getCriticalRate() {
        return getQuality()
                * (getTemplate().getCriticalRate() + generatedForLevel * getTemplate().getCriticalRate() * getTemplate().getCriticalRatePercentage() / 100_000)
                / 100_000;
    }

    public Long getCriticalDamage() {
        return getQuality()
                * (getTemplate().getCriticalDamage() + generatedForLevel * getTemplate().getCriticalDamage() * getTemplate().getCriticalDamagePercentage() / 100_000)
                / 100_000;
    }

    public Long getAccuracy() {
        return getQuality()
                * (getTemplate().getAccuracy() + generatedForLevel * getTemplate().getAccuracy() * getTemplate().getAccuracyPercentage() / 100_000)
                / 100_000;
    }

    public Long getResistance() {
        return getQuality()
                * (getTemplate().getResistance() + generatedForLevel * getTemplate().getResistance() * getTemplate().getResistancePercentage() / 100_000)
                / 100_000;
    }

}
