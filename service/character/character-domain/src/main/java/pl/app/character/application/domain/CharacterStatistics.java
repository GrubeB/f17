package pl.app.character.application.domain;

import lombok.Getter;
import pl.app.common.shared.model.StatisticType;
import pl.app.common.shared.model.Statistics;

import static pl.app.common.shared.model.StatisticType.*;

@Getter
public class CharacterStatistics extends Statistics {

    @SuppressWarnings("unused")
    public CharacterStatistics() {
        super();
    }

    public CharacterStatistics(Long persistence, Long durability, Long strength, Long speed, Long criticalRate, Long criticalDamage, Long accuracy, Long resistance) {
        super(persistence, durability, strength, speed, criticalRate, criticalDamage, accuracy, resistance);
    }

    @Override
    public void add(Long statisticQuantity, StatisticType statisticType) {
        if(statisticType.equals(SPEED)
        ||  statisticType.equals(CRITICAL_RATE)
        ||  statisticType.equals(CRITICAL_DAMAGE)
        ||  statisticType.equals(ACCURACY)
        ||  statisticType.equals(RESISTANCE)){
            throw CharacterException.UnmodifiableStatisticException.fromName(statisticType.name());
        }
        add(statisticQuantity, statisticType);
    }
    public void addStatisticForLevel() {
        this.setPersistence(getPersistence() + 1);
        this.setDurability(getDurability() + 1);
        this.setDurability(getDurability() + 1);
        this.setStrength(getStrength() + 1);
    }
}
