package pl.app.resource.village_resource.application.domain;

import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.app.resource.resource.application.domain.Resource;
import pl.app.resource.resource.application.domain.ResourceType;

import java.time.Duration;
import java.time.Instant;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
@Document(collection = "village_resource")
public class VillageResource {
    @Id
    private ObjectId villageId;
    private Resource resource;
    private Map<ResourceType, Instant> lastRefresh;

    public VillageResource() {
    }

    public VillageResource(ObjectId villageId, Resource resource) {
        this.villageId = villageId;
        this.resource = resource;
        var now = Instant.now();
        this.lastRefresh = EnumSet.allOf(ResourceType.class).stream()
                .collect(Collectors.toMap(
                        type -> type,
                        type -> now,
                        (oldValue, newValue) -> oldValue,
                        () -> new EnumMap<>(ResourceType.class)
                ));
    }

    public Resource refreshResource(Resource production, Resource resourceMax) {
        Resource resourceToAdd = new Resource(
                refreshResource(production.getWood(), ResourceType.WOOD),
                refreshResource(production.getClay(), ResourceType.CLAY),
                refreshResource(production.getIron(), ResourceType.IRON),
                0
        );
        addResource(resourceToAdd, resourceMax);
        return resourceToAdd;
    }

    private int refreshResource(Integer production, ResourceType type) {
        if (production <= 0) {
            lastRefresh.put(type, Instant.now());
            return 0;
        }
        Instant last = lastRefresh.get(type);
        Instant now = Instant.now();
        Duration timeElapsed = Duration.between(last, now);
        double secondsElapsed = timeElapsed.toSeconds();
        double productionPerSecond = (double) production / 3_600;
        int totalProduction = (int) (productionPerSecond * secondsElapsed);
        Duration actualTimeElapsed = Duration.ofSeconds(totalProduction * 3_600L / production);
        lastRefresh.put(type, last.plus(actualTimeElapsed));
        return totalProduction;
    }


    public void addResource(Resource resourceToAdd, Resource resourceMax) {
        if (Objects.isNull(resourceToAdd)) {
            return;
        }
        if (resourceToAdd.getWood() < 0 || resourceToAdd.getClay() < 0 || resourceToAdd.getIron() < 0 || resourceToAdd.getProvision() < 0) {
            throw new VillageResourceException.InvalidAmountException();
        }

        int newWood = Math.min(this.resource.getWood() + resourceToAdd.getWood(), resourceMax.getWood());
        int newClay = Math.min(this.resource.getClay() + resourceToAdd.getClay(), resourceMax.getClay());
        int newIron = Math.min(this.resource.getIron() + resourceToAdd.getIron(), resourceMax.getIron());
        int newProvision = Math.min(this.resource.getProvision() + resourceToAdd.getProvision(), resourceMax.getProvision());

        this.resource = new Resource(newWood, newClay, newIron, newProvision);
    }

    public void subtractResource(Resource resourceToSubtract) throws IllegalArgumentException {
        if (Objects.isNull(resourceToSubtract)) {
            return;
        }
        if (resourceToSubtract.getWood() < 0 || resourceToSubtract.getClay() < 0 || resourceToSubtract.getIron() < 0 || resourceToSubtract.getProvision() < 0) {
            throw new VillageResourceException.InvalidAmountException();
        }

        int newWood = this.resource.getWood() - resourceToSubtract.getWood();
        int newClay = this.resource.getClay() - resourceToSubtract.getClay();
        int newIron = this.resource.getIron() - resourceToSubtract.getIron();
        int newProvision = this.resource.getProvision() - resourceToSubtract.getProvision();

        if (newWood < 0 || newClay < 0 || newIron < 0 || newProvision < 0) {
            throw new VillageResourceException.InsufficientResourceException();
        }
        this.resource = new Resource(newWood, newClay, newIron, newProvision);
    }
}
