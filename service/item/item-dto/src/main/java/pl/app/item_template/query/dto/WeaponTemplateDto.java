package pl.app.item_template.query.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WeaponTemplateDto extends OutfitTemplateDto implements Serializable {
    protected Integer minDmg;
    protected Integer minDmgPercentage;
    protected Integer maxDmg;
    protected Integer maxDmgPercentage;

}
