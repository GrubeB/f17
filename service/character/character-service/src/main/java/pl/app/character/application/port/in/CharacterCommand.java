package pl.app.character.application.port.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import pl.app.character.application.domain.CharacterProfession;

import java.io.Serializable;


public interface CharacterCommand {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class CreateCharacterCommand implements Serializable {
        private String name;
        private CharacterProfession profession;
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class RemoveCharacterCommand implements Serializable {
        private ObjectId characterId;
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class AddStatisticCommand implements Serializable {
        private ObjectId characterId;
        private String statisticName;
        private Long statisticQuantity;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class AddExpCommand implements Serializable {
        private ObjectId characterId;
        private Long amount;
    }
}
