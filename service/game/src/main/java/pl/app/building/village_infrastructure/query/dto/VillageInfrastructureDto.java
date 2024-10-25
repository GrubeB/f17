package pl.app.building.village_infrastructure.query.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import pl.app.building.building.application.domain.Buildings;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VillageInfrastructureDto implements Serializable {
    private ObjectId villageId;
    private Buildings buildings;
}