package pl.app.god.application.domain;

import lombok.Getter;

@Getter
public class GodMoney {
    private Long amount;

    @SuppressWarnings("unused")
    public GodMoney() {
        this.amount = 0L;
    }

    public GodMoney(Long amount) {
        this.amount = amount;
    }


    public Long getBalance() {
        return amount;
    }

    public void addMoney(Long amount) {
        this.amount = this.amount + amount;
    }

    public void subtractMoney(Long amount) {
        if (amount < 0) {
            throw new GodException.InvalidAmountException();
        }
        if (this.amount - amount < 0) {
            throw new GodException.InsufficientMoneyException();
        }
        this.amount = this.amount - amount;
    }
}
