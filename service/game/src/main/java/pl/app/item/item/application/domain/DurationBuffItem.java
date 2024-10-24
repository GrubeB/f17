package pl.app.item.item.application.domain;

import lombok.Getter;

import java.time.Duration;
import java.util.Objects;
@Getter
public class DurationBuffItem extends Item {
    private Duration duration;

    public DurationBuffItem() {
    }

    public DurationBuffItem(ItemType itemType, Duration duration) {
        super(itemType);
        this.duration = duration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DurationBuffItem that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(duration, that.duration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), duration);
    }
}
