package pl.app.item_template.query.dto;

import lombok.*;
import org.bson.types.ObjectId;

import java.io.Serializable;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WeaponTemplateDto extends OutfitTemplateDto implements Serializable {
    protected Integer minDmg;
    protected Integer maxDmg;

}
