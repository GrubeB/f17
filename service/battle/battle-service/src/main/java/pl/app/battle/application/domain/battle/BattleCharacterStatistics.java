package pl.app.battle.application.domain.battle;

import lombok.Getter;
@Getter
public class BattleCharacterStatistics {
    private Long persistence;
    private Long durability;
    private Long strength;
    private Long speed;
    private Long criticalRate;
    private Long criticalDamage;
    private Long accuracy;
    private Long resistance;

    public BattleCharacterStatistics(Long persistence, Long durability, Long strength, Long speed, Long criticalRate, Long criticalDamage, Long accuracy, Long resistance) {
        this.persistence = persistence;
        this.durability = durability;
        this.strength = strength;
        this.speed = speed;
        this.criticalRate = criticalRate;
        this.criticalDamage = criticalDamage;
        this.accuracy = accuracy;
        this.resistance = resistance;
    }
}
