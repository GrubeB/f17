package pl.app.character_status.application.port.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import pl.app.character_status.application.domain.CharacterStatusType;

import java.io.Serializable;


public interface CharacterStatusCommand {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class ChangeCharacterStatusCommand implements Serializable {
        private ObjectId characterId;
        private CharacterStatusType type;
    }
}
