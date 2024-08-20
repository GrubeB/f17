package pl.app.character.application.domain;

import lombok.Getter;

@Getter
public class CharacterStatistics {
    private Long persistence;
    private Long durability;
    private Long strength;
    private Long speed;
    private Long criticalRate;
    private Long criticalDamage;
    private Long accuracy;
    private Long resistance;

    public CharacterStatistics() {
        this.persistence = 1L;
        this.durability = 1L;
        this.strength = 1L;
        this.speed = 100L;
        this.criticalRate = 1500L;
        this.criticalDamage = 5000L;
        this.accuracy = 1500L;
        this.resistance = 3000L;
    }

    public CharacterStatistics(Long persistence, Long durability, Long strength, Long speed, Long criticalRate, Long criticalDamage, Long accuracy, Long resistance) {
        this.persistence = persistence;
        this.durability = durability;
        this.strength = strength;
        this.speed = speed;
        this.criticalRate = criticalRate;
        this.criticalDamage = criticalDamage;
        this.accuracy = accuracy;
        this.resistance = resistance;
    }

    public void addStatistic(Long statisticQuantity, String statisticName) {
        StatisticType statisticTypeFromName = getStatisticTypeFromName(statisticName);
        switch (statisticTypeFromName) {
            case PERSISTENCE -> this.persistence = this.persistence + statisticQuantity;
            case DURABILITY -> this.durability = this.durability + statisticQuantity;
            case STRENGTH -> this.strength = this.strength + statisticQuantity;
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
        this.persistence = this.persistence + 1;
        this.durability = this.durability + 1;
        this.strength = this.strength + 1;
    }
}
