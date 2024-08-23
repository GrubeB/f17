package pl.app.item.application.port.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.io.Serializable;


public interface ItemCommand {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class CreateOutfitCommand implements Serializable {
        private ObjectId templateId;
        private Integer level;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class CreateWeaponCommand implements Serializable {
        private ObjectId templateId;
        private Integer level;
    }
}
