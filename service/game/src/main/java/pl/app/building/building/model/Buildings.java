package pl.app.building.building.model;

import lombok.Builder;
import lombok.Getter;
import pl.app.building.building.model.building.*;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

import static pl.app.building.building.model.BuildingType.*;

@Getter
@Builder
public class Buildings {
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

    public Buildings() {
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

    public Buildings(AcademyBuilding academy, BarracksBuilding barracks, ChapelBuilding chapel, ChurchBuilding church, ClayPitBuilding clayPit, FarmBuilding farm, HeadquartersBuilding headquarters, HospitalBuilding hospital, IronMineBuilding ironMine, MarketBuilding market, RallyPointBuilding rallyPoint, StatueBuilding statue, TavernBuilding tavern, TimberCampBuilding timberCamp, WallBuilding wall, WarehouseBuilding warehouse) {
        this.academy = Objects.nonNull(academy) ? academy : new AcademyBuilding();
        this.barracks = Objects.nonNull(barracks) ? barracks : new BarracksBuilding();
        this.chapel = Objects.nonNull(chapel) ? chapel : new ChapelBuilding();
        this.church = Objects.nonNull(church) ? church : new ChurchBuilding();
        this.clayPit = Objects.nonNull(clayPit) ? clayPit : new ClayPitBuilding();
        this.farm = Objects.nonNull(farm) ? farm : new FarmBuilding();
        this.headquarters = Objects.nonNull(headquarters) ? headquarters : new HeadquartersBuilding();
        this.hospital = Objects.nonNull(hospital) ? hospital : new HospitalBuilding();
        this.ironMine = Objects.nonNull(ironMine) ? ironMine : new IronMineBuilding();
        this.market = Objects.nonNull(market) ? market : new MarketBuilding();
        this.rallyPoint = Objects.nonNull(rallyPoint) ? rallyPoint : new RallyPointBuilding();
        this.statue = Objects.nonNull(statue) ? statue : new StatueBuilding();
        this.tavern = Objects.nonNull(tavern) ? tavern : new TavernBuilding();
        this.timberCamp = Objects.nonNull(timberCamp) ? timberCamp : new TimberCampBuilding();
        this.wall = Objects.nonNull(wall) ? wall : new WallBuilding();
        this.warehouse = Objects.nonNull(warehouse) ? warehouse : new WarehouseBuilding();
    }

    public Map<BuildingType, Building> buildings() {
        Map<BuildingType, Building> buildings = new EnumMap<>(BuildingType.class);
        buildings.put(ACADEMY, academy);
        buildings.put(BARRACKS, barracks);
        buildings.put(CHAPEL, chapel);
        buildings.put(CHURCH, church);
        buildings.put(CLAY_PIT, clayPit);
        buildings.put(FARM, farm);
        buildings.put(HEADQUARTERS, headquarters);
        buildings.put(HOSPITAL, hospital);
        buildings.put(IRON_MINE, ironMine);
        buildings.put(MARKET, market);
        buildings.put(RALLY_POINT, rallyPoint);
        buildings.put(STATUE, statue);
        buildings.put(TAVERN, tavern);
        buildings.put(TIMBER_CAMP, timberCamp);
        buildings.put(WALL, wall);
        buildings.put(WAREHOUSE, warehouse);
        return buildings;
    }

    public boolean meetRequirements(BuildingType type, Integer level) {
        return buildings().get(type).meetRequirements(level);
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
