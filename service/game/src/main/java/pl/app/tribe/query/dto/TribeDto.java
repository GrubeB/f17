package pl.app.tribe.query.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import pl.app.tribe.application.domain.Tribe;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TribeDto implements Serializable {
    private ObjectId id;
    private String name;
    private String abbreviation;
    private List<MemberDto> members;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MemberDto implements Serializable {
        private ObjectId playerId;
        private Tribe.MemberType type;
    }
}