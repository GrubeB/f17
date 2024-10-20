package pl.app.unit.village_army.query.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VillageArmyDto implements Serializable {
    private ObjectId villageId;
    private Integer spearmanNumber;
    private Integer swordsmanNumber;
    private Integer archerNumber;
    private Integer heavyCavalryNumber;
    private Integer axeFighterNumber;
    private Integer lightCavalryNumber;
    private Integer mountedArcherNumber;
    private Integer ramNumber;
    private Integer catapultNumber;
    private Integer paladinNumber;
    private Integer noblemanNumber;
    private Integer berserkerNumber;
    private Integer trebuchetNumber;
}