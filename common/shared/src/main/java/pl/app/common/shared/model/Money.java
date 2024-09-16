package pl.app.common.shared.model;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public class Money {
    private Map<Type, Long> monies;

    public enum Type {
        BASE,
        PREMIUM,
    }

    private Money(Map<Type, Long> monies) {
        this.monies = Collections.unmodifiableMap(new EnumMap<>(monies));
    }

    public Money() {
        this(createDefaultMonies());
    }

    public Money(Money money) {
        this.monies = money.getMonies();
    }

    public Money(Type type, Long amount) {
        Map<Type, Long> tempMonies = createDefaultMonies();
        tempMonies.put(type, amount);
        this.monies = Collections.unmodifiableMap(tempMonies);
    }

    public Money(Type type1, Long amount1, Type type2, Long amount2) {
        Map<Type, Long> tempMonies = createDefaultMonies();
        tempMonies.put(type1, amount1);
        tempMonies.put(type2, amount2);
        this.monies = Collections.unmodifiableMap(tempMonies);
    }

    private static Map<Type, Long> createDefaultMonies() {
        Map<Type, Long> monies = new EnumMap<>(Type.class);
        monies.put(Type.BASE, 0L);
        monies.put(Type.PREMIUM, 0L);
        return monies;
    }

    public long getBalance(Type type) {
        return monies.getOrDefault(type, 0L);
    }

    public Money addMoney(Type type, Long amount) {
        Map<Type, Long> newMonies = new EnumMap<>(this.monies);
        newMonies.put(type, getBalance(type) + amount);
        return new Money(newMonies);
    }

    public Money subtractMoney(Type type, Long amount) {
        Map<Type, Long> newMonies = new EnumMap<>(this.monies);
        newMonies.put(type, getBalance(type) - amount);
        return new Money(newMonies);
    }

    public Money addMoney(Money other) {
        Map<Type, Long> newMonies = new EnumMap<>(this.monies);
        for (Type type : Type.values()) {
            newMonies.put(type, this.getBalance(type) + other.getBalance(type));
        }
        return new Money(newMonies);
    }

    public Money multiplyMoney(Long multiplier) {
        Map<Type, Long> newMonies = new EnumMap<>(this.monies);
        for (Type type : Type.values()) {
            newMonies.put(type, getBalance(type) * multiplier);
        }
        return new Money(newMonies);
    }

    public Money multiplyMoney(Type type, Long multiplier) {
        Map<Type, Long> newMonies = new EnumMap<>(this.monies);
        newMonies.put(type, getBalance(type) * multiplier);
        return new Money(newMonies);
    }

    public Money divideMoney(Long divisor) {
        if (divisor == 0) {
            throw new IllegalArgumentException("Divisor cannot be zero");
        }
        Map<Type, Long> newMonies = new EnumMap<>(this.monies);
        for (Type type : Type.values()) {
            newMonies.put(type, getBalance(type) / divisor);
        }
        return new Money(newMonies);
    }

    public Money divideMoney(Type type, Long divisor) {
        if (divisor == 0) {
            throw new IllegalArgumentException("Divisor cannot be zero");
        }
        Map<Type, Long> newMonies = new EnumMap<>(this.monies);
        newMonies.put(type, getBalance(type) / divisor);
        return new Money(newMonies);
    }

    public Map<Type, Long> getMonies() {
        return this.monies;
    }

    @SuppressWarnings("unused")
    public void setMonies(Map<Type, Long> monies) {
        this.monies = Collections.unmodifiableMap(new EnumMap<>(monies));
    }
}