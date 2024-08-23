package pl.app.common.shared.model;


import java.text.MessageFormat;
import java.util.Objects;

public class Statistics {
    private Long persistence;
    private Long durability;
    private Long strength;
    private Long speed;
    private Long criticalRate;
    private Long criticalDamage;
    private Long accuracy;
    private Long resistance;

    public Statistics() {
        this.persistence = 0L;
        this.durability = 0L;
        this.strength = 0L;
        this.speed = 0L;
        this.criticalRate = 0L;
        this.criticalDamage = 0L;
        this.accuracy = 0L;
        this.resistance = 0L;
    }

    public Statistics(Long persistence, Long durability, Long strength, Long speed, Long criticalRate, Long criticalDamage, Long accuracy, Long resistance) {
        this.persistence = persistence;
        this.durability = durability;
        this.strength = strength;
        this.speed = speed;
        this.criticalRate = criticalRate;
        this.criticalDamage = criticalDamage;
        this.accuracy = accuracy;
        this.resistance = resistance;
    }

    private StatisticType getStatisticTypeFromName(String statisticName) {
        try {
            return StatisticType.valueOf(statisticName);
        } catch (IllegalArgumentException e) {
            throw new pl.app.common.shared.exception.IllegalArgumentException(MessageFormat.format("there is not statistic of name: {0}", statisticName));
        }
    }

    public Statistics mergeWith(Statistics statistics) {
        if (Objects.isNull(statistics)) {
            return this;
        }
        if (Objects.nonNull(statistics.getPersistence())) {
            if (Objects.nonNull(this.persistence)) {
                this.persistence = this.persistence + statistics.persistence;
            } else {
                this.persistence = statistics.persistence;
            }
        }
        if (Objects.nonNull(statistics.getDurability())) {
            if (Objects.nonNull(this.durability)) {
                this.durability = this.durability + statistics.durability;
            } else {
                this.durability = statistics.durability;
            }
        }
        if (Objects.nonNull(statistics.getStrength())) {
            if (Objects.nonNull(this.strength)) {
                this.strength = this.strength + statistics.strength;
            } else {
                this.strength = statistics.strength;
            }
        }
        if (Objects.nonNull(statistics.getSpeed())) {
            if (Objects.nonNull(this.speed)) {
                this.speed = this.speed + statistics.speed;
            } else {
                this.speed = statistics.speed;
            }
        }
        if (Objects.nonNull(statistics.getCriticalRate())) {
            if (Objects.nonNull(this.criticalRate)) {
                this.criticalRate = this.criticalRate + statistics.criticalRate;
            } else {
                this.criticalRate = statistics.criticalRate;
            }
        }
        if (Objects.nonNull(statistics.getCriticalDamage())) {
            if (Objects.nonNull(this.criticalDamage)) {
                this.criticalDamage = this.criticalDamage + statistics.criticalDamage;
            } else {
                this.criticalDamage = statistics.criticalDamage;
            }
        }
        if (Objects.nonNull(statistics.getAccuracy())) {
            if (Objects.nonNull(this.accuracy)) {
                this.accuracy = this.accuracy + statistics.accuracy;
            } else {
                this.accuracy = statistics.accuracy;
            }
        }
        if (Objects.nonNull(statistics.getResistance())) {
            if (Objects.nonNull(this.resistance)) {
                this.resistance = this.resistance + statistics.resistance;
            } else {
                this.resistance = statistics.resistance;
            }
        }
        return this;
    }


    public Long getPersistence() {
        return persistence;
    }

    public void setPersistence(Long persistence) {
        this.persistence = persistence;
    }

    public Long getDurability() {
        return durability;
    }

    public void setDurability(Long durability) {
        this.durability = durability;
    }

    public Long getStrength() {
        return strength;
    }

    public void setStrength(Long strength) {
        this.strength = strength;
    }

    public Long getSpeed() {
        return speed;
    }

    public void setSpeed(Long speed) {
        this.speed = speed;
    }

    public Long getCriticalRate() {
        return criticalRate;
    }

    public void setCriticalRate(Long criticalRate) {
        this.criticalRate = criticalRate;
    }

    public Long getCriticalDamage() {
        return criticalDamage;
    }

    public void setCriticalDamage(Long criticalDamage) {
        this.criticalDamage = criticalDamage;
    }

    public Long getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(Long accuracy) {
        this.accuracy = accuracy;
    }

    public Long getResistance() {
        return resistance;
    }

    public void setResistance(Long resistance) {
        this.resistance = resistance;
    }
}
