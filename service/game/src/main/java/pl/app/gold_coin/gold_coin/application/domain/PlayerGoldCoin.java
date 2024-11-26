package pl.app.gold_coin.gold_coin.application.domain;

import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.app.resource.resource.application.domain.Resource;

@Getter
@Document(collection = "player_gold_coin")
public class PlayerGoldCoin {
    public static final Resource GOLD_COIN_COST = new Resource(20_000, 20_000, 20_000, 0);
    @Id
    private ObjectId playerId;
    private Integer amount;

    public PlayerGoldCoin() {
    }

    public PlayerGoldCoin(ObjectId playerId) {
        this.playerId = playerId;
        this.amount = 0;
    }

    public void add(Integer amount) {
        this.amount = this.amount + amount;
    }

    // TODO
    public int getMaxNumberOfNobleMans() {
        return findClosestSum(amount);
    }

    private int sumOfSeries(int n) {
        int sum = 0;
        for (int i = 1; i <= n; i++) {
            sum += i;
        }
        return sum;
    }

    private int findClosestSum(int n) {
        int m = 0;
        while (sumOfSeries(m) <= n) {
            m++;
        }
        return m - 1;
    }
}
