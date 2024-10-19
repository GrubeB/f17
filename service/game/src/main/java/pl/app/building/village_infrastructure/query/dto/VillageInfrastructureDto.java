package pl.app.building.village_infrastructure.query.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import pl.app.building.building.application.domain.building.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VillageInfrastructureDto implements Serializable {
    private ObjectId villageId;
    private AcademyBuilding academy;
    private BarracksBuilding barracks;
    private ChapelBuilding chapel;
    private ChurchBuilding church;
    private ClayPitBuilding clayPit;
    private FarmBuilding farm;
    private HeadquartersBuilding headquarters;
    private HospitalBuilding hospital;
    private IronMineBuilding ironMine;
    private MarketBuilding market;
    private RallyPointBuilding rallyPoint;
    private StatueBuilding statue;
    private TavernBuilding tavern;
    private TimberCampBuilding timberCamp;
    private WallBuilding wall;
    private WarehouseBuilding warehouse;
}