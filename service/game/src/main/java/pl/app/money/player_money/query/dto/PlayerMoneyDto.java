package pl.app.money.player_money.query.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import pl.app.money.money.application.domain.Money;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlayerMoneyDto implements Serializable {
    private ObjectId playerId;
    private Money money;
}