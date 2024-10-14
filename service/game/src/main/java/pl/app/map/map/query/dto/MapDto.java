package pl.app.map.map.query.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import pl.app.map.map.application.domain.MapObjectType;
import pl.app.map.map.application.domain.Position;
import pl.app.map.village_position.query.dto.VillagePositionDto;

import java.io.Serializable;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MapDto implements Serializable {
    private Set<Position> positions;
    private Set<ProvinceDto> provinces;
    private Set<MapObjectDto> mapObjects;
    private Set<VillagePositionDto> villagePositions;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ProvinceDto implements Serializable {
        private ObjectId id;
        private String name;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class MapObjectDto implements Serializable {
        private ObjectId id;
        private Position position;
        private MapObjectType type;
    }
}