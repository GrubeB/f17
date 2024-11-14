package pl.app.map.map.application.domain;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.app.map.village_position.application.domain.VillagePosition;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Document(collection = "map")
public class Map {
    @Id
    private ObjectId id;
    private Integer height;
    private Integer width;
    private Set<Position> positions;
    private Set<Province> provinces;
    @Setter
    private Set<MapObject> mapObjects;

    @Transient
    @Setter
    private Set<VillagePosition> villagePositions;

    public Map() {
    }

    public Map(Integer width, Integer height, Set<Province> provinces, Set<Position> positions) {
        this.id = ObjectId.get();
        this.width = width;
        this.height = height;
        this.positions = positions;
        this.provinces = provinces;
        this.mapObjects = new LinkedHashSet<>();
        this.villagePositions = new LinkedHashSet<>();
    }

    public Position getPosition(Integer x, Integer y) {
        return positions.stream().filter(p -> p.getX().equals(x) && p.getY().equals(y))
                .findAny()
                .orElseThrow(MapException.NotFoundPositionException::new);
    }

    public Set<Position> getOccupiedPositions() {
        return Stream.of(
                        mapObjects.stream().map(MapObject::getPosition),
                        villagePositions.stream().map(VillagePosition::getPosition)
                )
                .flatMap(Function.identity())
                .collect(Collectors.toSet());
    }

    public Set<Position> getNoOccupiedPositions() {
        Set<Position> occupiedPositions = getOccupiedPositions();
        return positions.stream()
                .filter(position -> !occupiedPositions.contains(position))
                .collect(Collectors.toSet());
    }

    public Optional<Position> getNextPosition() {
        Set<Position> noOccupiedPositions = getNoOccupiedPositions();
        if (noOccupiedPositions.isEmpty()) {
            return Optional.empty();
        }
        Position center = new Position(height / 2, width / 2, null);
        Position newPosition = noOccupiedPositions.iterator().next();
        double min = Position.calculateDistance(newPosition, center);
        for (Position p : noOccupiedPositions) {
            double m = Position.calculateDistance(p, center);
            if (m < min) {
                newPosition = p;
                min = m;
            }
        }
        return Optional.of(newPosition);
    }
}
