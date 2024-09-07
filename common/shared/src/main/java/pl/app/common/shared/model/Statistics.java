package pl.app.common.shared.model;


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

    public static Long getHp(Long persistence, CharacterProfession profession) {
        return switch (profession) {
            case MARKSMAN -> persistence * 40L;
            case WARRIOR -> persistence * 50L;
            case MAGE -> persistence * 35L;
            case SUPPORT -> persistence * 45L;
            case TANK -> persistence * 60L;
            default -> throw new IllegalStateException("Unexpected value: " + profession);
        };
    }

    public static Long getAttackPower(Long strength, CharacterProfession profession) {
        return (long) switch (profession) {
            case MARKSMAN -> strength * 3.5;
            case WARRIOR -> strength * 2.0;
            case MAGE -> strength * 4.0;
            case SUPPORT -> strength * 2.0;
            case TANK -> strength * 2.0;
            default -> throw new IllegalStateException("Unexpected value: " + profession);
        };
    }

    public static Long getDef(Long durability, CharacterProfession profession) {
        return switch (profession) {
            case MARKSMAN -> durability * 4L;
            case WARRIOR -> durability * 5L;
            case MAGE -> durability * 3L;
            case SUPPORT -> durability * 3L;
            case TANK -> durability * 5L;
            default -> throw new IllegalStateException("Unexpected value: " + profession);
        };
    }

    public static Statistics zero() {
        return new Statistics(0L,0L, 0L,0L,0L, 0L,0L,0L);
    }

    public void add(Long statisticQuantity) {
        this.setPersistence(getPersistence() + statisticQuantity);
        this.setDurability(getDurability() + statisticQuantity);
        this.setStrength(getStrength() + statisticQuantity);
        this.setSpeed(getSpeed() + statisticQuantity);
        this.setCriticalRate(getCriticalRate() + statisticQuantity);
        this.setCriticalDamage(getCriticalDamage() + statisticQuantity);
        this.setAccuracy(getAccuracy() + statisticQuantity);
        this.setResistance(getResistance() + statisticQuantity);
    }

    public void add(Long statisticQuantity, StatisticType statisticType) {
        switch (statisticType) {
            case PERSISTENCE -> this.setPersistence(getPersistence() + statisticQuantity);
            case DURABILITY -> this.setDurability(getDurability() + statisticQuantity);
            case STRENGTH -> this.setStrength(getStrength() + statisticQuantity);
            case SPEED -> this.setSpeed(getSpeed() + statisticQuantity);
            case CRITICAL_RATE -> this.setCriticalRate(getCriticalRate() + statisticQuantity);
            case CRITICAL_DAMAGE -> this.setCriticalDamage(getCriticalDamage() + statisticQuantity);
            case ACCURACY -> this.setAccuracy(getAccuracy() + statisticQuantity);
            case RESISTANCE -> this.setResistance(getResistance() + statisticQuantity);
        }
    }

    public void multiply(Long number) {
        this.setPersistence(getPersistence() * number);
        this.setDurability(getDurability() * number);
        this.setStrength(getStrength() * number);
        this.setSpeed(getSpeed() * number);
        this.setCriticalRate(getCriticalRate() * number);
        this.setCriticalDamage(getCriticalDamage() * number);
        this.setAccuracy(getAccuracy() * number);
        this.setResistance(getResistance() * number);
    }

    public void multiply(Long number, StatisticType statisticType) {
        switch (statisticType) {
            case PERSISTENCE -> this.setPersistence(getPersistence() * number);
            case DURABILITY -> this.setDurability(getDurability() * number);
            case STRENGTH -> this.setStrength(getStrength() * number);
            case SPEED -> this.setSpeed(getSpeed() * number);
            case CRITICAL_RATE -> this.setCriticalRate(getCriticalRate() * number);
            case CRITICAL_DAMAGE -> this.setCriticalDamage(getCriticalDamage() * number);
            case ACCURACY -> this.setAccuracy(getAccuracy() * number);
            case RESISTANCE -> this.setResistance(getResistance() * number);
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

    /* GETTERS & SETTERS */
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
