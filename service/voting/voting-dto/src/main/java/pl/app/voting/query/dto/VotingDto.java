package pl.app.voting.query.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.io.Serializable;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VotingDto implements Serializable {
    private ObjectId id;
    private String domainObjectType;
    private String domainObjectId;
    private Set<VoteCounterDto> votes;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class VoteCounterDto implements Serializable {
        private String type;
        private Long number;
    }
}
