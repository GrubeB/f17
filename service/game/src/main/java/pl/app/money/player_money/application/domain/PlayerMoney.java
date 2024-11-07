package pl.app.money.player_money.application.domain;

import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.app.money.money.application.domain.Money;

@Getter
@Document(collection = "player_money")
public class PlayerMoney {
    @Id
    private ObjectId playerId;
    private Money money;

    public PlayerMoney() {
    }

    public PlayerMoney(ObjectId playerId, Money money) {
        this.playerId = playerId;
        this.money = money;
    }

    public void add(Money money) {
        if (money.getBalance() < 0) {
            throw new PlayerMoneyException.InvalidAmountException();
        }
        this.money = this.money.addMoney(money);
    }

    public void subtract(Money money) {
        if (money.getBalance() < 0) {
            throw new PlayerMoneyException.InvalidAmountException();
        }

        this.money = this.money.subtraactMoney(money);
        if (this.money.getBalance() < 0) {
            throw new PlayerMoneyException.InsufficientMoneyException();
        }
    }
}
