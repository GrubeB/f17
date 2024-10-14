package pl.app.resource.village_resource.application.domain;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.app.resource.resource.application.domain.Resource;

import java.util.Objects;

@Getter
@Document(collection = "village_resource")
public class VillageResource {
    @Id
    private ObjectId villageId;
    private Resource resource;
    @Setter
    private Resource resourceMax;

    public VillageResource() {
    }

    public VillageResource(ObjectId villageId, Resource resource, Resource resourceMax) {
        this.villageId = villageId;
        this.resource = resource;
        this.resourceMax = resourceMax;
    }
    public void addResource(Resource resourceToAdd) {
        if (Objects.isNull(resourceToAdd)) {
            return;
        }
        if (resourceToAdd.getWood() < 0 || resourceToAdd.getClay() < 0 || resourceToAdd.getIron() < 0 || resourceToAdd.getProvision() < 0) {
            throw new VillageResourceException.InvalidAmountException();
        }

        int newWood = Math.min(this.resource.getWood() + resourceToAdd.getWood(), this.resourceMax.getWood());
        int newClay = Math.min(this.resource.getClay() + resourceToAdd.getClay(), this.resourceMax.getClay());
        int newIron = Math.min(this.resource.getIron() + resourceToAdd.getIron(), this.resourceMax.getIron());
        int newProvision = Math.min(this.resource.getProvision() + resourceToAdd.getProvision(), this.resourceMax.getProvision());

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
