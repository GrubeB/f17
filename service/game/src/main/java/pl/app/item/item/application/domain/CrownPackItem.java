package pl.app.item.item.application.domain;

import lombok.Getter;

import java.util.Objects;

@Getter
public class CrownPackItem extends Item {
    private Integer value;

    public CrownPackItem() {
    }

    public CrownPackItem( Integer value) {
        super(ItemType.CROWN_PACK);
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CrownPackItem that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), value);
    }
}
