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

    public static Statistics zero() {
        return new Statistics(0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L);
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

    public Statistics add(Long statisticQuantity, StatisticType statisticType) {
        return switch (statisticType) {
            case PERSISTENCE ->
                    new Statistics(this.getPersistence() + statisticQuantity, this.getDurability(), this.getStrength(), this.getSpeed(),
                            this.getCriticalRate(), this.getCriticalDamage(), this.getAccuracy(), this.getResistance());
            case DURABILITY ->
                    new Statistics(this.getPersistence(), this.getDurability() + statisticQuantity, this.getStrength(), this.getSpeed(),
                            this.getCriticalRate(), this.getCriticalDamage(), this.getAccuracy(), this.getResistance());
            case STRENGTH ->
                    new Statistics(this.getPersistence(), this.getDurability(), this.getStrength() + statisticQuantity, this.getSpeed(),
                            this.getCriticalRate(), this.getCriticalDamage(), this.getAccuracy(), this.getResistance());
            case SPEED ->
                    new Statistics(this.getPersistence(), this.getDurability(), this.getStrength(), this.getSpeed() + statisticQuantity,
                            this.getCriticalRate(), this.getCriticalDamage(), this.getAccuracy(), this.getResistance());
            case CRITICAL_RATE ->
                    new Statistics(this.getPersistence(), this.getDurability(), this.getStrength(), this.getSpeed(),
                            this.getCriticalRate() + statisticQuantity, this.getCriticalDamage(), this.getAccuracy(), this.getResistance());
            case CRITICAL_DAMAGE ->
                    new Statistics(this.getPersistence(), this.getDurability(), this.getStrength(), this.getSpeed(),
                            this.getCriticalRate(), this.getCriticalDamage() + statisticQuantity, this.getAccuracy(), this.getResistance());
            case ACCURACY ->
                    new Statistics(this.getPersistence(), this.getDurability(), this.getStrength(), this.getSpeed(),
                            this.getCriticalRate(), this.getCriticalDamage(), this.getAccuracy() + statisticQuantity, this.getResistance());
            case RESISTANCE ->
                    new Statistics(this.getPersistence(), this.getDurability(), this.getStrength(), this.getSpeed(),
                            this.getCriticalRate(), this.getCriticalDamage(), this.getAccuracy(), this.getResistance() + statisticQuantity);
        };
    }

    public Statistics add(Statistics statistics) {
        if (Objects.isNull(statistics)) {
            return this;
        }
        Long persistence = this.persistence;
        Long durability = this.durability;
        Long strength = this.strength;
        Long speed = this.speed;
        Long criticalRate = this.criticalRate;
        Long criticalDamage = this.criticalDamage;
        Long accuracy = this.accuracy;
        Long resistance = this.resistance;
        if (Objects.nonNull(statistics.getPersistence())) {
            if (Objects.nonNull(this.persistence)) {
                persistence = this.persistence + statistics.persistence;
            } else {
                persistence = statistics.persistence;
            }
        }
        if (Objects.nonNull(statistics.getDurability())) {
            if (Objects.nonNull(this.durability)) {
                durability = this.durability + statistics.durability;
            } else {
                durability = statistics.durability;
            }
        }
        if (Objects.nonNull(statistics.getStrength())) {
            if (Objects.nonNull(this.strength)) {
                strength = this.strength + statistics.strength;
            } else {
                strength = statistics.strength;
            }
        }
        if (Objects.nonNull(statistics.getSpeed())) {
            if (Objects.nonNull(this.speed)) {
                speed = this.speed + statistics.speed;
            } else {
                speed = statistics.speed;
            }
        }
        if (Objects.nonNull(statistics.getCriticalRate())) {
            if (Objects.nonNull(this.criticalRate)) {
                criticalRate = this.criticalRate + statistics.criticalRate;
            } else {
                criticalRate = statistics.criticalRate;
            }
        }
        if (Objects.nonNull(statistics.getCriticalDamage())) {
            if (Objects.nonNull(this.criticalDamage)) {
                criticalDamage = this.criticalDamage + statistics.criticalDamage;
            } else {
                criticalDamage = statistics.criticalDamage;
            }
        }
        if (Objects.nonNull(statistics.getAccuracy())) {
            if (Objects.nonNull(this.accuracy)) {
                accuracy = this.accuracy + statistics.accuracy;
            } else {
                accuracy = statistics.accuracy;
            }
        }
        if (Objects.nonNull(statistics.getResistance())) {
            if (Objects.nonNull(this.resistance)) {
                resistance = this.resistance + statistics.resistance;
            } else {
                resistance = statistics.resistance;
            }
        }
        return new Statistics(persistence, durability, strength, speed, criticalRate, criticalDamage, accuracy, resistance);
    }

    public Statistics multiply(Long number) {
        return new Statistics(this.getPersistence() * number, this.getDurability() * number, this.getStrength() * number, this.getSpeed() * number,
                this.getCriticalRate() * number, this.getCriticalDamage() * number, this.getAccuracy() * number, this.getResistance() * number);
    }

    public Statistics multiply(Long number, StatisticType statisticType) {
        return switch (statisticType) {
            case PERSISTENCE ->
                    new Statistics(this.getPersistence() * number, this.getDurability(), this.getStrength(), this.getSpeed(),
                            this.getCriticalRate(), this.getCriticalDamage(), this.getAccuracy(), this.getResistance());
            case DURABILITY ->
                    new Statistics(this.getPersistence(), this.getDurability() * number, this.getStrength(), this.getSpeed(),
                            this.getCriticalRate(), this.getCriticalDamage(), this.getAccuracy(), this.getResistance());
            case STRENGTH ->
                    new Statistics(this.getPersistence(), this.getDurability(), this.getStrength() * number, this.getSpeed(),
                            this.getCriticalRate(), this.getCriticalDamage(), this.getAccuracy(), this.getResistance());
            case SPEED ->
                    new Statistics(this.getPersistence(), this.getDurability(), this.getStrength(), this.getSpeed() * number,
                            this.getCriticalRate(), this.getCriticalDamage(), this.getAccuracy(), this.getResistance());
            case CRITICAL_RATE ->
                    new Statistics(this.getPersistence(), this.getDurability(), this.getStrength(), this.getSpeed(),
                            this.getCriticalRate() * number, this.getCriticalDamage(), this.getAccuracy(), this.getResistance());
            case CRITICAL_DAMAGE ->
                    new Statistics(this.getPersistence(), this.getDurability(), this.getStrength(), this.getSpeed(),
                            this.getCriticalRate(), this.getCriticalDamage() * number, this.getAccuracy(), this.getResistance());
            case ACCURACY ->
                    new Statistics(this.getPersistence(), this.getDurability(), this.getStrength(), this.getSpeed(),
                            this.getCriticalRate(), this.getCriticalDamage(), this.getAccuracy() * number, this.getResistance());
            case RESISTANCE ->
                    new Statistics(this.getPersistence(), this.getDurability(), this.getStrength(), this.getSpeed(),
                            this.getCriticalRate(), this.getCriticalDamage(), this.getAccuracy(), this.getResistance() * number);
        };
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
