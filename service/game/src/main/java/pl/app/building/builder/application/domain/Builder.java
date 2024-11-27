package pl.app.building.builder.application.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.app.building.building.model.BuildingLevel;
import pl.app.building.building.model.BuildingType;
import pl.app.building.building.model.Buildings;
import pl.app.resource.share.Resource;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@Document(collection = "builder")
@NoArgsConstructor
public class Builder {
    @Id
    private ObjectId villageId;
    private Integer constructNumberMax;
    private Set<Construct> constructs = new LinkedHashSet<>();


    public Builder(ObjectId villageId, Integer constructNumberMax) {
        this.villageId = villageId;
        this.constructNumberMax = constructNumberMax;
        this.constructs = new LinkedHashSet<>();
    }

    public Construct addConstruct(BuildingType type, Buildings buildings, Map<Integer, ? extends BuildingLevel> buildingLevels) {
        if (constructs.size() >= constructNumberMax) {
            throw new BuilderException.ReachedMaxConstructorNumberException();
        }
        var currentBuilding = buildings.getBuildingByType(type);
        var toLevel = calculateToLevel(type, currentBuilding.getLevel());
        var buildingLevel = buildingLevels.get(toLevel);
        verifyVillageMeetsRequirements(buildings, buildingLevel.getRequirements());
        Optional<Construct> lastConstruct = getLastConstruct();
        if (lastConstruct.isPresent()) {
            Instant lastConstructToDate = lastConstruct.get().getTo();
            Construct newConstruct = new Construct(type, false, toLevel, lastConstructToDate, lastConstructToDate.plus(buildingLevel.getDuration()), buildingLevel.getCost());
            constructs.add(newConstruct);
            return newConstruct;
        } else {
            Instant now = Instant.now();
            Construct newConstruct = new Construct(type, false, toLevel, now, now.plus(buildingLevel.getDuration()), buildingLevel.getCost());
            constructs.add(newConstruct);
            return newConstruct;
        }
    }

    public Construct starFirstConstruct() {
        Optional<Construct> firstConstruct = getFirstConstruct();
        if (firstConstruct.isEmpty()) {
            throw new BuilderException.FailedToStartBuildingConstructionException("failed to start building construction, because there is no constructions");
        }
        if (firstConstruct.get().getStarted()) {
            throw new BuilderException.FailedToStartBuildingConstructionException("failed to start building construction because first building is under construction");
        }
        firstConstruct.get().setStarted(true);
        return firstConstruct.get();
    }

    public Set<Construct> cancelConstruct(BuildingType type, Integer toLevel) {
        Instant now = Instant.now();
        Set<Construct> constructsToCancel = constructs.stream()
                .filter(e -> Objects.equals(e.getType(), type))
                .filter(e -> e.getToLevel() >= toLevel)
                .collect(Collectors.toSet());
        this.constructs.removeAll(constructsToCancel);
        if (!constructsToCancel.isEmpty()) {
            rescheduleConstructions(now);
        }
        return constructsToCancel;
    }

    private void rescheduleConstructions(Instant now) {
        LinkedList<Construct> collect = constructs.stream()
                .sorted(Comparator.comparing(Construct::getFrom))
                .collect(Collectors.toCollection(LinkedList::new));
        for (Construct c : collect) {
            Duration difference = Duration.between(c.getFrom(), c.getTo());
            c.setFrom(now);
            c.setTo(now.plus(difference));
            now = now.plus(difference);
        }
    }

    public Set<Construct> rejectConstruct(BuildingType type) {
        Instant now = Instant.now();
        Set<Construct> constructsToReject = constructs.stream()
                .filter(e -> Objects.equals(e.getType(), type))
                .collect(Collectors.toSet());
        this.constructs.removeAll(constructsToReject);
        if (!constructsToReject.isEmpty()) {
            rescheduleConstructions(now);
        }
        return constructsToReject;
    }

    public Optional<Construct> finishFirstConstruct() {
        Optional<Construct> first = getFirstConstruct();
        if (first.isPresent() && first.get().getStarted() && first.get().getTo().isBefore(Instant.now())) {
            this.constructs.remove(first.get());
            return first;
        }
        return Optional.empty();
    }


    private void verifyVillageMeetsRequirements(Buildings buildings, Set<BuildingLevel.Requirement> requirements) {
        var meetRequirements = requirements.stream()
                .allMatch(requirement -> buildings.meetRequirements(requirement.getBuildingType(), requirement.getLevel()));
        if (!meetRequirements) {
            throw new BuilderException.VillageDoseNotMeetRequirementsException();
        }
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

    public Optional<Construct> getFirstConstruct() {
        return constructs.stream().min(Comparator.comparing(Construct::getTo));
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Construct {
        private BuildingType type;
        private Boolean started;
        private Integer toLevel;
        private Instant from;
        private Instant to;
        private Resource cost;
    }
}
