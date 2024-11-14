package pl.app.money.gold_coin.query.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlayerGoldCoinDto implements Serializable {
    private ObjectId playerId;
    private Integer amount;
    private Integer maxNumberOfNobleMans;
}