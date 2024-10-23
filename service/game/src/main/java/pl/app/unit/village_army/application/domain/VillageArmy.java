package pl.app.unit.village_army.application.domain;

import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.app.unit.unit.application.domain.Army;

@Getter
@Document(collection = "village_army")
public class VillageArmy {
    @Id
    private ObjectId villageId;
    private Army villageArmy;
    private Army supportArmy;
    private Army blockedArmy; // armies that are on the expedition

    public VillageArmy() {
    }

    public VillageArmy(ObjectId villageId) {
        this.villageId = villageId;
        this.villageArmy = Army.zero();
        this.supportArmy = Army.zero();
        this.blockedArmy = Army.zero();
    }

    public void add(Army army) {
        villageArmy.add(army);
    }

    public void subtract(Army army) {
        villageArmy.subtract(army);
    }

    public void block(Army army) {
        villageArmy.subtract(army);
        blockedArmy.add(army);
    }

    public void unblock(Army army) {
        blockedArmy.subtract(army);
        villageArmy.add(army);
    }
}
