package pl.app.battle.application.port.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.io.Serializable;


public interface BattleCommand {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class StartDuelBattleCommand implements Serializable {
        private ObjectId player1;
        private ObjectId player2;
    }
}
