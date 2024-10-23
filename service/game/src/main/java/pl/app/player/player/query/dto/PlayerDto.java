package pl.app.player.player.query.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import pl.app.unit.unit.application.domain.Army;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlayerDto implements Serializable {
    private ObjectId playerId;
    private String accountId;
}