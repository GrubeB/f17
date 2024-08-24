package pl.app.god_applicant_collection.application.domain;

import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Document(collection = "god_applicant")
public class GodApplicant {
    @Id
    private ObjectId id;
    private ObjectId characterId;

    @SuppressWarnings("unused")
    public GodApplicant() {
    }

    public GodApplicant(ObjectId characterId) {
        this.id = ObjectId.get();
        this.characterId = characterId;
    }
}
