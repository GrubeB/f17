package pl.app.god_applicant_collection.application.domain;

import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.app.character.application.domain.Character;

@Getter
@Document(collection = "god_applicant")
public class GodApplicant {
    @Id
    private ObjectId id;
    private Character character;

    @SuppressWarnings("unused")
    public GodApplicant() {
    }

    public GodApplicant(Character character) {
        this.id = ObjectId.get();
        this.character = character;
    }
}
