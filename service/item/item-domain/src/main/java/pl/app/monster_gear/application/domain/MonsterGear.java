package pl.app.monster_gear.application.domain;

import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.app.gear.aplication.domain.Gear;

@Getter
@Document(collection = "gears")
public class MonsterGear extends Gear {
    private ObjectId monsterId;

    @SuppressWarnings("unused")
    public MonsterGear() {
        super();
    }

    public MonsterGear(ObjectId monsterId) {
        super();
        this.monsterId = monsterId;
    }
}
