package pl.app.inventory.shared;

import lombok.Getter;
import pl.app.resource.share.ResourceType;

import java.time.Duration;
import java.util.Objects;

@Getter
public class ResourceBuffItem extends DurationBuffItem {
    private ResourceType resourceType;
    private Integer value;

    public ResourceBuffItem() {
    }

    public ResourceBuffItem(Duration duration, ResourceType resourceType, Integer value) {
        super(ItemType.RESOURCE_BUFF, duration);
        this.resourceType = resourceType;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ResourceBuffItem that)) return false;
        if (!super.equals(o)) return false;
        return resourceType == that.resourceType && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), resourceType, value);
    }
}
