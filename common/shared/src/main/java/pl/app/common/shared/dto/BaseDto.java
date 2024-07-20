package pl.app.common.shared.dto;

import java.io.Serializable;
import java.util.UUID;

public class BaseDto implements
        Serializable {
    private UUID id;

    public BaseDto() {
    }

    public BaseDto(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
