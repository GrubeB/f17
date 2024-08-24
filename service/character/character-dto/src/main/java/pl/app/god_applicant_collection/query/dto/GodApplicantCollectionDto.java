package pl.app.god_applicant_collection.query.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.io.Serializable;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GodApplicantCollectionDto implements Serializable {
    private ObjectId id;
    private ObjectId godId;
    private Set<GodApplicantDto> applicants;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GodApplicantDto implements Serializable {
        private ObjectId id;
        private ObjectId characterId;
    }
}
