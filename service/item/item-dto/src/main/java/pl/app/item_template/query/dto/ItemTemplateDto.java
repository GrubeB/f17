package pl.app.item_template.query.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import pl.app.common.shared.model.Money;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemTemplateDto implements Serializable {
    protected ObjectId id;
    protected String type;
    protected String name;
    protected String description;
    protected String imageId;
    protected Money money;
    private Long moneyPercentage;
}
