package pl.app.unit.village_army.query.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import pl.app.unit.unit.application.domain.Army;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VillageArmyDto implements Serializable {
    private ObjectId villageId;
    private Army villageArmy;
    private Army supportArmy;
    private Army blockedArmy;
}