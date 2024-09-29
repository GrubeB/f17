package pl.app.character_status.application.domain;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import pl.app.character_template.application.domain.CharacterTemplate;

@Document(collection = "character_status")
@Getter
public class CharacterStatus {
    @Id
    private ObjectId id;
    private ObjectId characterId;
    @Setter
    private CharacterStatusType type;

    @SuppressWarnings("unused")
    public CharacterStatus() {
    }

    public CharacterStatus(ObjectId characterId) {
        this.id = ObjectId.get();
        this.characterId = characterId;
        this.type = CharacterStatusType.IDLE;
    }
}
