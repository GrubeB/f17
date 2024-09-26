package pl.app.item.application.port.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import pl.app.common.shared.model.ItemType;

import java.io.Serializable;
import java.util.Set;


public interface ItemCommand {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class CreateItemCommand implements Serializable {
        private ObjectId templateId;
        private Integer level;
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class CreateRandomItemCommand implements Serializable {
        private Integer numberOfItems;
        private Set<ItemType> itemTypes;
        private Integer level;
    }

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
