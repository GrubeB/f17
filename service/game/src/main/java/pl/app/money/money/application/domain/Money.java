package pl.app.money.money.application.domain;

public class Money {
    private Integer amount;

    public Money() {
        this.amount = 0;
    }

    public Money(Integer amount) {
        this.amount = amount;
    }

    public Money(Money money) {
        this.amount = money.amount;
    }

    public int getBalance() {
        return this.amount;
    }

    public Money addMoney(Integer amount) {
        return new Money(this.amount + amount);
    }

    public Money addMoney(Money money) {
        return new Money(this.amount + money.amount);
    }

    public Money subtraactMoney(Integer amount) {
        return new Money(this.amount - amount);
    }

    public Money subtraactMoney(Money money) {
        return new Money(this.amount - money.amount);
    }
}
