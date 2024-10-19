package pl.app.building.builder.application.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.app.building.building.application.domain.BuildingLevel;
import pl.app.building.building.application.domain.BuildingType;
import pl.app.resource.resource.application.domain.Resource;

import java.time.Instant;
import java.util.*;

@Getter
@Document(collection = "builder")
@NoArgsConstructor
public class Builder {
    @Id
    private ObjectId villageId;
    private Integer constructNumberMax;
    private Set<Construct> constructs;


    public Builder(ObjectId villageId, Integer constructNumberMax) {
        this.villageId = villageId;
        this.constructNumberMax = constructNumberMax;
        this.constructs = new LinkedHashSet<>();
    }

    public Construct addConstruct(BuildingType type, Integer currentLevel, Map<Integer, ? extends BuildingLevel> buildingLevels) {
        if (constructs.size() >= constructNumberMax) {
            throw new BuilderException.ReachedMaxConstructorNumberException();
        }
        var toLevel = calculateToLevel(type, currentLevel);
        var buildingLevel = buildingLevels.get(toLevel);
        Optional<Construct> lastConstruct = getLastConstruct();
        if (lastConstruct.isPresent()) {
            Instant lastConstructToDate = lastConstruct.get().getTo();
            Construct newConstruct = new Construct(type, toLevel, lastConstructToDate, lastConstructToDate.plus(buildingLevel.getDuration()), buildingLevel.getCost());
            constructs.add(newConstruct);
            return newConstruct;
        } else {
            Instant now = Instant.now();
            Construct newConstruct = new Construct(type, toLevel, now, now.plus(buildingLevel.getDuration()), buildingLevel.getCost());
            constructs.add(newConstruct);
            return newConstruct;
        }
    }

    public Construct removeConstruct(BuildingType type) {
        Optional<Construct> max = constructs.stream()
                .filter(e -> Objects.equals(e.getType(), type))
                .max(Comparator.comparing(Construct::getToLevel));
        if (max.isPresent()) {
            this.constructs.remove(max.get());
            return max.get();
        }
        return null;
    }

    private Integer calculateToLevel(BuildingType type, Integer currentLevel) {
        Optional<Construct> max = constructs.stream()
                .filter(e -> Objects.equals(e.getType(), type))
                .max(Comparator.comparing(Construct::getToLevel));
        return max
                .map(construct -> construct.getToLevel() + 1)
                .orElseGet(() -> currentLevel + 1);
    }

    private Optional<Construct> getLastConstruct() {
        return constructs.stream().max(Comparator.comparing(Construct::getTo));
    }

    public Optional<Construct> finishConstruct() {
        Optional<Construct> first = getFirstConstruct();
        if (first.isPresent() && first.get().getTo().isBefore(Instant.now())) {
            this.constructs.remove(first.get());
            return first;
        }
        return Optional.empty();
    }

    public Optional<Construct> getFirstConstruct() {
        return constructs.stream()
                .min(Comparator.comparing(Construct::getTo));
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Construct {
        private BuildingType type;
        private Integer toLevel;
        private Instant from;
        private Instant to;
        private Resource cost;
    }
}
