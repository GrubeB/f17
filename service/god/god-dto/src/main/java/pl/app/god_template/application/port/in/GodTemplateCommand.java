package pl.app.god_template.application.port.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import pl.app.common.shared.model.Money;

import java.io.Serializable;


public interface GodTemplateCommand {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class CreateGodTemplateCommand implements Serializable {
        private String name;
        private String description;
        private String imageId;
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class UpdateGodTemplateCommand implements Serializable {
        private ObjectId id;
        private String name;
        private String description;
        private String imageId;
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class DeleteGodTemplateCommand implements Serializable {
        private ObjectId id;
    }
}
