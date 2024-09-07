package pl.app.god_equipment.application.domain;

import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.app.gear.aplication.domain.Gear;

@Getter
@Document(collection = "gears")
public class CharacterGear extends Gear {
    private ObjectId characterId;

    @SuppressWarnings("unused")
    public CharacterGear() {
        super();
    }

    public CharacterGear(ObjectId characterId) {
        super();
        this.characterId = characterId;
    }
}
