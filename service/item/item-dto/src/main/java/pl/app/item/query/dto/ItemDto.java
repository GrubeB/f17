package pl.app.item.query.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto implements Serializable {
    protected ObjectId id;
    protected String type;
    protected ObjectId templateId;
    protected String name;
    protected String description;
    protected String imageId;
}
