package pl.app.unit.village_army.application.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.app.unit.unit.application.domain.Army;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Getter
@Document(collection = "village_army")
@NoArgsConstructor
public class VillageArmy {
    @Id
    private ObjectId villageId;
    private Army villageArmy;
    private Army supportArmy;
    private List<VillageSupport> villageSupports;
    private Army blockedArmy; // armies that are on the expedition

    public VillageArmy(ObjectId villageId) {
        this.villageId = villageId;
        this.villageArmy = Army.zero();
        this.supportArmy = Army.zero();
        this.blockedArmy = Army.zero();
        this.villageSupports = new LinkedList<>();
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

    public void addSupport(ObjectId villageId, Army army) {
        supportArmy.add(army);
        Optional<VillageSupport> villageSupportOpt = getVillageSupport(villageId);
        if (villageSupportOpt.isPresent()) {
            villageSupportOpt.get().getArmy().add(army);
            return;
        }
        villageSupports.add(new VillageSupport(villageId, army));
    }

    public void removeSupport(ObjectId villageId, Army army) {
        Optional<VillageSupport> villageSupportOpt = getVillageSupport(villageId);
        if (villageSupportOpt.isEmpty()) {
            throw new VillageArmyException.VillageDoseNotSupportException();
        }
        VillageSupport villageSupport = villageSupportOpt.get();
        villageSupport.getArmy().subtract(army);
        supportArmy.subtract(army);
        if (villageSupport.getArmy().isEmpty()) {
            villageSupports.remove(villageSupport);
        }
    }

    private Optional<VillageSupport> getVillageSupport(ObjectId villageId) {
        return villageSupports.stream().filter(e -> Objects.equals(e.getVillageId(), villageId)).findAny();
    }

    @Getter
    public static class VillageSupport {
        private ObjectId villageId;
        private Army army;

        public VillageSupport(ObjectId villageId, Army army) {
            this.villageId = villageId;
            this.army = army;
        }
    }
}
