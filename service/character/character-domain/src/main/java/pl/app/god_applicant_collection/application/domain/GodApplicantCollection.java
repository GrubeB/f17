package pl.app.god_applicant_collection.application.domain;

import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

@Getter
@Document(collection = "god_applicant_collection")
public class GodApplicantCollection {
    @Id
    private ObjectId id;
    private ObjectId godId;

    @DocumentReference
    private Set<GodApplicant> applicants;

    @SuppressWarnings("unused")
    public GodApplicantCollection() {
    }

    public GodApplicantCollection(ObjectId godId) {
        this.id = ObjectId.get();
        this.godId = godId;
        this.applicants = new LinkedHashSet<>();
    }

    public GodApplicant addApplicant(GodApplicant applicant) {
        this.applicants.add(applicant);
        return applicant;
    }

    public GodApplicant removeApplicantByCharacterId(ObjectId characterId) {
        GodApplicant applicant = this.getApplicantByIdOrThrow(characterId);
        this.applicants.remove(applicant);
        return applicant;
    }
    public GodApplicant acceptApplicantByCharacterId(ObjectId characterId) {
        GodApplicant applicant = this.getApplicantByIdOrThrow(characterId);
        this.applicants.remove(applicant);
        return applicant;
    }
    public GodApplicant rejectApplicantByCharacterId(ObjectId characterId) {
        GodApplicant applicant = this.getApplicantByIdOrThrow(characterId);
        this.applicants.remove(applicant);
        return applicant;
    }
    private GodApplicant getApplicantByIdOrThrow(ObjectId characterId) {
        return getApplicantById(characterId)
                .orElseThrow(() -> GodApplicantCollectionException.NotFoundGodApplicantException.fromCharacterId(characterId.toHexString()));
    }

    private Optional<GodApplicant> getApplicantById(ObjectId characterId) {
        return this.applicants.stream().filter(a -> a.getCharacterId().equals(characterId))
                .findAny();
    }
}
