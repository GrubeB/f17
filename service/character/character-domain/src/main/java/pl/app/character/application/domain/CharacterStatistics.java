package pl.app.character.application.domain;

import lombok.Getter;
import pl.app.common.shared.model.StatisticType;
import pl.app.common.shared.model.Statistics;

@Getter
public class CharacterStatistics {
    private Statistics statistics;

    @SuppressWarnings("unused")
    public CharacterStatistics() {
        this.statistics = new Statistics();
    }

    public CharacterStatistics(Long persistence, Long durability, Long strength, Long speed, Long criticalRate, Long criticalDamage, Long accuracy, Long resistance) {
        this.statistics = new Statistics(persistence, durability, strength, speed, criticalRate, criticalDamage, accuracy, resistance);
    }

    public void addStatistic(Long statisticQuantity, String statisticName) {
        StatisticType statisticTypeFromName = getStatisticTypeFromName(statisticName);
        switch (statisticTypeFromName) {
            case PERSISTENCE -> this.setPersistence(getPersistence() + statisticQuantity);
            case DURABILITY -> this.setDurability(getDurability() + statisticQuantity);
            case STRENGTH -> this.setStrength(getStrength() + statisticQuantity);
            case SPEED, CRITICAL_RATE, CRITICAL_DAMAGE, ACCURACY, RESISTANCE -> {
                throw CharacterException.UnmodifiableStatisticException.fromName(statisticName);
            }
        }
    }

    private StatisticType getStatisticTypeFromName(String statisticName) {
        try {
            return StatisticType.valueOf(statisticName);
        } catch (IllegalArgumentException e) {
            throw CharacterException.NoSuchStatisticException.fromName(statisticName);
        }
    }

    public void addStatisticForLevel() {
        this.setPersistence(getPersistence() + 1);
        this.setDurability(getDurability() + 1);
        this.setDurability(getDurability() + 1);
        this.setStrength(getStrength() + 1);
    }

    public Long getPersistence() {
        return statistics.getPersistence();
    }

    public void setPersistence(Long persistence) {
        statistics.setPersistence(persistence);
    }

    public Long getDurability() {
        return statistics.getDurability();
    }

    public void setDurability(Long durability) {
        statistics.setDurability(durability);
    }

    public Long getStrength() {
        return statistics.getStrength();
    }

    public void setStrength(Long strength) {
        statistics.setStrength(strength);
    }

    public Long getSpeed() {
        return statistics.getSpeed();
    }

    public void setSpeed(Long speed) {
        statistics.setSpeed(speed);
    }

    public Long getCriticalRate() {
        return statistics.getCriticalRate();
    }

    public void setCriticalRate(Long criticalRate) {
        statistics.setCriticalRate(criticalRate);
    }

    public Long getCriticalDamage() {
        return statistics.getCriticalDamage();
    }

    public void setCriticalDamage(Long criticalDamage) {
        statistics.setCriticalDamage(criticalDamage);
    }

    public Long getAccuracy() {
        return statistics.getAccuracy();
    }

    public void setAccuracy(Long accuracy) {
        statistics.setAccuracy(accuracy);
    }

    public Long getResistance() {
        return statistics.getResistance();
    }

    public void setResistance(Long resistance) {
        statistics.setResistance(resistance);
    }
}
