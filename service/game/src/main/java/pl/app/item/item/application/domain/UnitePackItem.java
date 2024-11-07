package pl.app.item.item.application.domain;

import lombok.Getter;
import pl.app.unit.unit.application.domain.UnitType;

import java.util.Objects;

@Getter
public class UnitePackItem extends Item {
    private UnitType unitType;
    private Integer value;

    public UnitePackItem() {
    }

    public UnitePackItem(UnitType unitType, Integer value) {
        super(ItemType.UNIT_PACK);
        this.unitType = unitType;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UnitePackItem that)) return false;
        if (!super.equals(o)) return false;
        return unitType == that.unitType && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), unitType, value);
    }
}
