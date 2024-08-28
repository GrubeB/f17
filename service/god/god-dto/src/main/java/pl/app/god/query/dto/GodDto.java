package pl.app.god.query.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import pl.app.common.shared.model.Money;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GodDto implements Serializable {
    protected ObjectId id;
    protected String name;
    private Money money;
}
