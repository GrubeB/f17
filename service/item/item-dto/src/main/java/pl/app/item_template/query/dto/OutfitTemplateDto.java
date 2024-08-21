package pl.app.item_template.query.dto;

import lombok.*;
import org.bson.types.ObjectId;

import java.io.Serializable;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OutfitTemplateDto extends ItemTemplateDto implements Serializable {
    private Long persistence;
    private Long persistencePercentage;
    private Long durability;
    private Long durabilityPercentage;
    private Long strength;
    private Long strengthPercentage;
    private Long speed;
    private Long speedPercentage;
    private Long criticalRate;
    private Long criticalRatePercentage;
    private Long criticalDamage;
    private Long criticalDamagePercentage;
    private Long accuracy;
    private Long accuracyPercentage;
    private Long resistance;
    private Long resistancePercentage;
}
