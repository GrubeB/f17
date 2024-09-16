package pl.app.god.application.domain;

import lombok.Getter;
import pl.app.common.shared.model.Money;

@Getter
public class GodMoney extends Money {
    public GodMoney() {
        super();
    }
    public GodMoney(Money money){
        super(money);
    }
    @Override
    public Money subtractMoney(Money.Type type, Long amount) {
        if (amount < 0) {
            throw new GodException.InvalidAmountException();
        }
        if (getBalance(type) - amount < 0) {
            throw new GodException.InsufficientMoneyException();
        }
        return super.subtractMoney(type, amount);
    }
}
