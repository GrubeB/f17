package pl.app.building.village_infrastructure.application.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.app.building.building.application.domain.Building;
import pl.app.building.building.application.domain.BuildingType;
import pl.app.building.building.application.domain.Buildings;
import pl.app.building.building.application.domain.building.*;

@Getter
@Document(collection = "village_infrastructure")
@NoArgsConstructor
public class VillageInfrastructure {
    @Id
    private ObjectId villageId;
    private Buildings buildings;

    public VillageInfrastructure(ObjectId villageId) {
        this.villageId = villageId;
        this.buildings = new Buildings();
    }
}
