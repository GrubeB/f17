package pl.app.unit.recruiter.query.dto;

import lombok.*;
import org.bson.types.ObjectId;
import pl.app.resource.share.Resource;
import pl.app.unit.unit.application.domain.Unit;

import java.io.Serializable;
import java.time.Instant;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecruiterDto implements Serializable {
    private ObjectId villageId;
    private Integer constructNumberMax;
    private Set<RequestDto> requests;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RequestDto implements Serializable {
        private Unit unit;
        private Integer amount;
        private Instant from;
        private Instant to;
        private Resource cost;
    }
}