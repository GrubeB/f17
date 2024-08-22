package pl.app.god.application.port.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.io.Serializable;


public interface GodCommand {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class CreateGodCommand implements Serializable {
        private ObjectId accountId;
        private String name;
    }
}
