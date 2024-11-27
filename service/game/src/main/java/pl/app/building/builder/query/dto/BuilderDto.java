package pl.app.building.builder.query.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import pl.app.building.building.model.BuildingType;
import pl.app.resource.share.Resource;

import java.io.Serializable;
import java.time.Instant;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BuilderDto implements Serializable {
    private ObjectId villageId;
    private Integer constructNumberMax;
    private Set<ConstructDto> constructs;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ConstructDto implements Serializable {
        private BuildingType type;
        private Boolean started;
        private Integer toLevel;
        private Instant from;
        private Instant to;
        private Resource cost;
    }
}