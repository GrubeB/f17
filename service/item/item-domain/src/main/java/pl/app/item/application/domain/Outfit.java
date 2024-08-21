package pl.app.item.application.domain;

import lombok.Getter;
import org.springframework.data.annotation.AccessType;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
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
        return getTemplate().getPersistence();
    }

    public Long getDurability() {
        return getTemplate().getDurability();
    }

    public Long getStrength() {
        return getTemplate().getStrength();
    }

    public Long getSpeed() {
        return getTemplate().getSpeed();
    }

    public Long getCriticalRate() {
        return getTemplate().getCriticalRate();
    }

    public Long getCriticalDamage() {
        return getTemplate().getCriticalDamage();
    }

    public Long getAccuracy() {
        return getTemplate().getAccuracy();
    }

    public Long getResistance() {
        return getTemplate().getResistance();
    }


}
