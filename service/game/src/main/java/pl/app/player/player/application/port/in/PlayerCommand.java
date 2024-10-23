package pl.app.player.player.application.port.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

public interface PlayerCommand {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class CreatePlayerCommand implements Serializable {
        private String accountId;
        private String name;
    }
}
