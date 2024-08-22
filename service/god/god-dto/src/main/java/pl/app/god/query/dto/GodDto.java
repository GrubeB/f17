package pl.app.god.query.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GodDto implements Serializable {
    protected ObjectId id;
    protected String name;
    private MoneyDto money;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MoneyDto implements Serializable {
        private Long amount;
    }
}
