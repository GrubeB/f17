package pl.app.player.player.service.dto;

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
public class PlayerDto implements Serializable {
    private ObjectId playerId;
    private String accountId;
    private String name;
    private String description;
}