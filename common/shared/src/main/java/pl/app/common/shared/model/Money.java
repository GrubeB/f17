package pl.app.common.shared.model;

import java.util.EnumMap;
import java.util.Map;

public class Money {
    private Map<Type, Long> monies = new EnumMap<>(Type.class);

    @SuppressWarnings("unused")
    public Money() {
        this.monies.put(Type.BASE, 0L);
        this.monies.put(Type.PREMIUM, 0L);
    }
    public Money(Type type, Long amount) {
        this.monies.put(Type.BASE, 0L);
        this.monies.put(Type.PREMIUM, 0L);
        this.addMoney(type, amount);
    }
    public Money(Type type1, Long amount1, Type type2, Long amount2) {
        this.monies.put(Type.BASE, 0L);
        this.monies.put(Type.PREMIUM, 0L);
        this.addMoney(type1, amount1);
        this.addMoney(type2, amount2);
    }

    public long getBalance(Type type) {
        Long amount = this.monies.get(type);
        if (amount == null) {
            return 0L;
        }
        return amount;
    }

    public void addMoney(Type type, Long amount) {
        long balance = getBalance(type);
        this.monies.put(type, balance + amount);
    }

    public void subtractMoney(Type type, Long amount) {
        long balance = getBalance(type);
        this.monies.put(type, balance - amount);
    }

    public enum Type {
        BASE,
        PREMIUM,
    }
}