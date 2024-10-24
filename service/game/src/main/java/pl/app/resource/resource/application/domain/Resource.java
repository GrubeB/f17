package pl.app.resource.resource.application.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

@Getter
@Setter
@ToString
public class Resource {
    private Integer wood;
    private Integer clay;
    private Integer iron;
    private Integer provision;

    public Resource() {
        this.wood = 0;
        this.clay = 0;
        this.iron = 0;
        this.provision = 0;
    }

    public Resource(Integer wood, Integer clay, Integer iron, Integer provision) {
        this.wood = wood;
        this.clay = clay;
        this.iron = iron;
        this.provision = provision;
    }

    public Resource(Integer value, ResourceType resourceType) {
        this.wood = 0;
        this.clay = 0;
        this.iron = 0;
        this.provision = 0;
        switch (resourceType){
            case WOOD -> this.wood = value;
            case CLAY -> this.clay = value;
            case IRON -> this.iron = value;
            case PROVISION -> this.provision = value;
        }
    }

    public static Resource zero() {
        return new Resource(0, 0, 0, 0);
    }

    public static Resource of(Integer value) {
        return new Resource(value, value, value, value);
    }

    public static Resource of(Integer wood, Integer clay, Integer iron, Integer provision) {
        return new Resource(wood, clay, iron, provision);
    }
    public static Resource of(Integer value, ResourceType resourceType) {
        return new Resource(value, resourceType);
    }

    public Resource add(Integer resourceQuantity, ResourceType resourceType) {
        return switch (resourceType) {
            case WOOD ->
                    new Resource(this.getWood() + resourceQuantity, this.getClay(), this.getIron(), this.getProvision());
            case CLAY ->
                    new Resource(this.getWood(), this.getClay() + resourceQuantity, this.getIron(), this.getProvision());
            case IRON ->
                    new Resource(this.getWood(), this.getClay(), this.getIron() + resourceQuantity, this.getProvision());
            case PROVISION ->
                    new Resource(this.getWood(), this.getClay(), this.getIron(), this.getProvision() + resourceQuantity);
        };
    }

    public Resource add(Resource resource) {
        if (Objects.isNull(resource)) {
            return this;
        }
        Integer wood = this.wood;
        Integer clay = this.clay;
        Integer iron = this.iron;
        Integer provision = this.provision;

        if (Objects.nonNull(resource.getWood())) {
            wood = this.wood + resource.wood;
        }
        if (Objects.nonNull(resource.getClay())) {
            clay = this.clay + resource.clay;
        }
        if (Objects.nonNull(resource.getIron())) {
            iron = this.iron + resource.iron;
        }
        if (Objects.nonNull(resource.getProvision())) {
            provision = this.provision + resource.provision;
        }

        return new Resource(wood, clay, iron, provision);
    }
    public Resource subtract(Resource resource) {
        if (Objects.isNull(resource)) {
            return this;
        }
        Integer wood = this.wood;
        Integer clay = this.clay;
        Integer iron = this.iron;
        Integer provision = this.provision;

        if (Objects.nonNull(resource.getWood())) {
            wood = this.wood - resource.wood;
        }
        if (Objects.nonNull(resource.getClay())) {
            clay = this.clay - resource.clay;
        }
        if (Objects.nonNull(resource.getIron())) {
            iron = this.iron - resource.iron;
        }
        if (Objects.nonNull(resource.getProvision())) {
            provision = this.provision - resource.provision;
        }

        return new Resource(wood, clay, iron, provision);
    }

    public Resource multiply(Integer number) {
        return new Resource(this.getWood() * number, this.getClay() * number, this.getIron() * number, this.getProvision() * number);
    }

    public Resource multiply(Integer number, ResourceType resourceType) {
        return switch (resourceType) {
            case WOOD -> new Resource(this.getWood() * number, this.getClay(), this.getIron(), this.getProvision());
            case CLAY -> new Resource(this.getWood(), this.getClay() * number, this.getIron(), this.getProvision());
            case IRON -> new Resource(this.getWood(), this.getClay(), this.getIron() * number, this.getProvision());
            case PROVISION ->
                    new Resource(this.getWood(), this.getClay(), this.getIron(), this.getProvision() * number);
        };
    }
}
