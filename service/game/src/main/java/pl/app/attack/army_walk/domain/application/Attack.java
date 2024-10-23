package pl.app.attack.army_walk.domain.application;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.app.attack.battle.application.domain.Battle;
import pl.app.resource.resource.application.domain.Resource;
import pl.app.unit.unit.application.domain.Army;
import pl.app.unit.unit.application.domain.Unit;
import pl.app.unit.unit.application.domain.UnitType;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

@Getter
@Document(collection = "attack")
public class Attack {
    @Id
    private ObjectId attackId;
    private ObjectId attackerId;
    private ObjectId defenderId;
    private ObjectId attackerVillageId;
    private ObjectId defenderVillageId;

    private Instant battleDate;
    private ArmyWalk atackArmyWalk;
    private ArmyWalk returnArmyWalk;
    private Battle.BattleResult battleResult;
    private Resource plunderedResource;

    public Attack(ArmyWalk atackArmyWalk,
                  Army army1, Army army2,
                  Map<UnitType, Unit> units,
                  AttackVillage defenderVillage
    ) {
        this.attackId = atackArmyWalk.getArmyWalkId();
        this.attackerId = atackArmyWalk.getFrom().getPlayerId();
        this.defenderId = atackArmyWalk.getTo().getPlayerId();
        this.attackerVillageId = atackArmyWalk.getFrom().getVillageId();
        this.defenderVillageId = atackArmyWalk.getTo().getVillageId();
        this.atackArmyWalk = atackArmyWalk;
        this.battleDate = Instant.now();

        Battle battle = new Battle(army1, army2, units, defenderVillage.getWallLevel());
        battle.setBattleBuffs(true, true, 100, 0, false, false);
        battleResult = battle.startBattle();

        Army finalAttackerArmy = battleResult.getFinalAttackerArmy();
        int armyCapacity = finalAttackerArmy.entrySet().stream().mapToInt(e -> units.get(e.getKey()).getCapacity() * e.getValue()).sum();
        plunderedResource = calculatePlunderedResource(armyCapacity, defenderVillage.getResource());

        if(battleResult.isAttackerWin()){
            returnArmyWalk = new ArmyWalk(ArmyWalkType.RETURN, units,
                    atackArmyWalk.getTo(),
                    atackArmyWalk.getFrom(),
                    battleResult.getFinalAttackerArmy(), plunderedResource
            );
        }
    }

    public Optional<ArmyWalk> getReturnArmyWalk() {
        return Optional.of(returnArmyWalk);
    }

    @Getter
    @AllArgsConstructor
    public static class AttackVillage {
        private Resource resource;
        private Integer wallLevel;
    }

    private Resource calculatePlunderedResource(int armyCapacity, Resource villageResource) {
        Resource plunderedResource = Resource.zero();
        // I
        int remainderCapacity = armyCapacity;
        int woodToPlunder1 = Math.min(remainderCapacity / 3, villageResource.getWood());
        int clayToPlunder1 = Math.min(remainderCapacity / 3, villageResource.getClay());
        int ironToPlunder1 = Math.min(remainderCapacity / 3, villageResource.getIron());
        remainderCapacity = remainderCapacity - (woodToPlunder1 + clayToPlunder1 + ironToPlunder1);
        plunderedResource = plunderedResource.add(Resource.of(woodToPlunder1, clayToPlunder1, ironToPlunder1, 0));
        villageResource = villageResource.subtract(Resource.of(woodToPlunder1, clayToPlunder1, ironToPlunder1, 0));
        // II
        int woodToPlunder2 = Math.min(remainderCapacity / 2, villageResource.getWood());
        int clayToPlunder2 = Math.min(remainderCapacity / 2, villageResource.getClay());
        int ironToPlunder2 = Math.min(remainderCapacity / 2, villageResource.getIron());
        remainderCapacity = remainderCapacity - (woodToPlunder2 + clayToPlunder2 + ironToPlunder2);
        plunderedResource = plunderedResource.add(Resource.of(woodToPlunder2, clayToPlunder2, ironToPlunder2, 0));
        villageResource = villageResource.subtract(Resource.of(woodToPlunder2, clayToPlunder2, ironToPlunder2, 0));
        // III
        int woodToPlunder3 = Math.min(remainderCapacity, villageResource.getWood());
        int clayToPlunder3 = Math.min(remainderCapacity, villageResource.getClay());
        int ironToPlunder3 = Math.min(remainderCapacity, villageResource.getIron());
        return plunderedResource.add(Resource.of(woodToPlunder3, clayToPlunder3, ironToPlunder3, 0));
    }

}
