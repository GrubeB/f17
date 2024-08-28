package pl.app.god.application.domain;

import lombok.Getter;
import pl.app.common.shared.model.Money;

@Getter
public class GodMoney extends Money {
    public GodMoney() {
        super();
    }
    @Override
    public void subtractMoney(Money.Type type, Long amount) {
        if (amount < 0) {
            throw new GodException.InvalidAmountException();
        }
        if (getBalance(type) - amount < 0) {
            throw new GodException.InsufficientMoneyException();
        }
        super.subtractMoney(type, amount);
    }
}
