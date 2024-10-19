package pl.app.building.village_infrastructure.application.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.app.building.building.application.domain.Building;
import pl.app.building.building.application.domain.BuildingType;
import pl.app.building.building.application.domain.building.*;

@Getter
@Document(collection = "village_infrastructure")
@NoArgsConstructor
public class VillageInfrastructure {
    @Id
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

    public VillageInfrastructure(ObjectId villageId) {
        this.villageId = villageId;

        this.academy = new AcademyBuilding();
        this.barracks = new BarracksBuilding();
        this.chapel = new ChapelBuilding();
        this.church = new ChurchBuilding();
        this.clayPit = new ClayPitBuilding();
        this.farm = new FarmBuilding();
        this.headquarters = new HeadquartersBuilding();
        this.hospital = new HospitalBuilding();
        this.ironMine = new IronMineBuilding();
        this.market = new MarketBuilding();
        this.rallyPoint = new RallyPointBuilding();
        this.statue = new StatueBuilding();
        this.tavern = new TavernBuilding();
        this.timberCamp = new TimberCampBuilding();
        this.wall = new WallBuilding();
        this.warehouse = new WarehouseBuilding();
    }

    public Building getBuildingByType(BuildingType type) {
        return switch (type) {
            case ACADEMY -> academy;
            case BARRACKS -> barracks;
            case CHAPEL -> chapel;
            case CHURCH -> church;
            case CLAY_PIT -> clayPit;
            case FARM -> farm;
            case HEADQUARTERS -> headquarters;
            case HOSPITAL -> hospital;
            case IRON_MINE -> ironMine;
            case MARKET -> market;
            case RALLY_POINT -> rallyPoint;
            case STATUE -> statue;
            case TAVERN -> tavern;
            case TIMBER_CAMP -> timberCamp;
            case WALL -> wall;
            case WAREHOUSE -> warehouse;
        };
    }
}
