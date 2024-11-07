package pl.app.item.item.application.domain;

import lombok.Getter;

import java.util.Objects;

@Getter
public class OfficerItem extends Item {
    private OfficerType officerType;

    public OfficerItem() {
    }

    public OfficerItem(OfficerType officerType) {
        super(ItemType.OFFICER);
        this.officerType = officerType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OfficerItem that)) return false;
        if (!super.equals(o)) return false;
        return officerType == that.officerType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), officerType);
    }
}
