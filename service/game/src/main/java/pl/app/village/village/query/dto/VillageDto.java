package pl.app.village.village.query.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import pl.app.map.village_position.query.dto.VillagePositionDto;
import pl.app.resource.village_resource.query.dto.VillageResourceDto;
import pl.app.village.village.application.domain.VillageType;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VillageDto implements Serializable {
    private ObjectId id;
    private VillageType type;
    private ObjectId ownerId;
    private VillagePositionDto villagePosition;
    private VillageResourceDto villageResource;
}