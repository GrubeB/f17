package pl.app.map.village_position.query.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import pl.app.map.map.application.domain.Position;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VillagePositionDto implements Serializable {
    private ObjectId villageId;
    private Position position;
}