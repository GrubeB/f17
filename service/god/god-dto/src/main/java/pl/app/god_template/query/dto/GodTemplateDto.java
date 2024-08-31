package pl.app.god_template.query.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import pl.app.common.shared.model.Money;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GodTemplateDto implements Serializable {
    protected ObjectId id;
    protected String name;
    protected String description;
    protected String imageId;
}
