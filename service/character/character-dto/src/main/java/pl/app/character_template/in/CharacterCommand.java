package pl.app.character_template.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import pl.app.common.shared.model.CharacterProfession;
import pl.app.common.shared.model.CharacterRace;

import java.io.Serializable;


public interface CharacterCommand {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class CreateCharacterTemplateCommand implements Serializable {
        private String name;
        private String description;
        private CharacterRace race;
        private CharacterProfession profession;
        private String imageId;
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class UpdateCharacterTemplateCommand implements Serializable {
        private ObjectId id;
        private String name;
        private String description;
        private CharacterRace race;
        private CharacterProfession profession;
        private String imageId;
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class DeleteCharacterTemplateCommand implements Serializable {
        private ObjectId id;
    }
}
