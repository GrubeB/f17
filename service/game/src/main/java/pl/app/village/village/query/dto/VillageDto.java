package pl.app.village.village.query.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import pl.app.building.village_infrastructure.query.dto.VillageInfrastructureDto;
import pl.app.map.village_position.query.dto.VillagePositionDto;
import pl.app.player.player.query.dto.PlayerDto;
import pl.app.resource.village_resource.query.dto.VillageResourceDto;
import pl.app.unit.village_army.query.dto.VillageArmyDto;
import pl.app.village.village.application.domain.VillageType;

import java.io.Serializable;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VillageDto implements Serializable {
    private ObjectId id;
    private VillageType type;
    private PlayerDto owner;
    private Integer loyalty;
    private VillagePositionDto villagePosition;
    private VillageResourceDto villageResource;
    private VillageInfrastructureDto villageInfrastructure;
    private VillageArmyDto villageArmy;

    public ObjectId getOwnerId() {
        return Objects.isNull(owner) ? null : owner.getPlayerId();
    }
}