package pl.app.item.item.application.domain;

import lombok.Getter;
import pl.app.resource.resource.application.domain.ResourceType;

import java.util.Objects;

@Getter
public class ResourcePackItem extends Item {
    private ResourceType resourceType;
    private Integer value;

    public ResourcePackItem() {
    }

    public ResourcePackItem(ResourceType resourceType, Integer value) {
        super(ItemType.RESOURCE_PACK);
        this.resourceType = resourceType;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ResourcePackItem that)) return false;
        if (!super.equals(o)) return false;
        return resourceType == that.resourceType && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), resourceType, value);
    }
}
