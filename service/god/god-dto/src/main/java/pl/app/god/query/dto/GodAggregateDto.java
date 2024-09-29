package pl.app.god.query.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import pl.app.energy.query.dto.EnergyDto;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GodAggregateDto implements Serializable {
    private ObjectId id;
    private GodDto god;
    private EnergyDto energy;
}
