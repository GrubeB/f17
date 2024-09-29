package pl.app.energy.query.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnergyDto implements Serializable {
    private ObjectId godId;
    private Long amount;
}
