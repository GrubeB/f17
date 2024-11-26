package pl.app.attack.army_walk.domain.application;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import pl.app.attack.battle.application.domain.Battle;
import pl.app.inventory.shared.Officers;
import pl.app.resource.resource.application.domain.Resource;
import pl.app.unit.unit.application.domain.Army;
import pl.app.unit.unit.application.domain.Unit;
import pl.app.unit.unit.application.domain.UnitType;

import java.time.Instant;
import java.time.LocalTime;
import java.util.Map;
import java.util.Random;

@Getter
@Document(collection = "attack")
public class Attack {
    private static final Random random = new Random();
    @Transient
    private final Map<UnitType, Unit> units;
    @Id
    private ObjectId attackId;
    private ObjectId attackerId;
    private ObjectId defenderId;
    private ObjectId attackerVillageId;
    private ObjectId defenderVillageId;
    private Instant battleDate;
    @DocumentReference
    private ArmyWalk atackArmyWalk;
    @DocumentReference
    private ArmyWalk returnArmyWalk;
    private Battle.BattleResult battleResult;
    private Resource plunderedResource = Resource.zero();
    private Boolean villageConquered = false;
    private Integer loyaltyDecrease = 0;

    public Attack(ArmyWalk atackArmyWalk,
                  Army deffenderArmy,
                  Map<UnitType, Unit> units,
                  DefenderVillage defenderVillage,
                  Boolean attackerFaithBonus, Boolean defenderFaithBonus
    ) {
        this.attackId = atackArmyWalk.getArmyWalkId();
        this.atackArmyWalk = atackArmyWalk;
        this.battleDate = Instant.now();

        this.attackerId = atackArmyWalk.getFrom().getPlayerId();
        this.defenderId = atackArmyWalk.getTo().getPlayerId();
        this.attackerVillageId = atackArmyWalk.getFrom().getVillageId();
        this.defenderVillageId = atackArmyWalk.getTo().getVillageId();
        this.units = units;

        Battle battle = new Battle(atackArmyWalk.getArmy(), deffenderArmy, units, defenderVillage.getWallLevel());
        battle.setBattleBuffs(attackerFaithBonus, defenderFaithBonus, 100, generateLuck(), atackArmyWalk.getOfficers().getGrandmaster(),
                atackArmyWalk.getOfficers().getMedic(), calculateNightBuf());
        battleResult = battle.startBattle();

        if (!battleResult.isAttackerWin()) {
            return;
        }
        if (battleResult.getFinalAttackerArmy().get(UnitType.NOBLEMAN) > 0) {
            loyaltyDecrease = 30;
        }
        if (battleResult.getFinalAttackerArmy().get(UnitType.NOBLEMAN) > 0 && defenderVillage.loyalty - loyaltyDecrease < 0) {
            villageConquered = true;
            battleResult.getFinalAttackerArmy().subtract(Army.of(Map.of(UnitType.NOBLEMAN, 1)));
        } else {
            int armyCapacity = calculateArmyCapacity(battleResult.getFinalAttackerArmy(), atackArmyWalk.getOfficers());
            plunderedResource = calculatePlunderedResource(armyCapacity, defenderVillage.getResource());
        }
    }

    private int generateLuck() {
        return random.nextInt(31) - 15;
    }

    private int calculateArmyCapacity(Army army, Officers officers) {
        var baseCapacity = army.entrySet().stream().mapToInt(e -> units.get(e.getKey()).getCapacity() * e.getValue()).sum();
        var capacityWithOfficers = baseCapacity * (officers.getMasterOfLoot() ? 1.5 : 1.0);
        return (int) capacityWithOfficers;
    }

    private boolean calculateNightBuf() {
        LocalTime start = LocalTime.of(22, 0);
        LocalTime end = LocalTime.of(6, 0);
        LocalTime now = LocalTime.now();
        return now.isAfter(start) || now.isBefore(end);
    }

    public ArmyWalk getReturnArmyWalk() {
        return new ArmyWalk(ArmyWalkType.RETURN, units,
                atackArmyWalk.getTo(),
                atackArmyWalk.getFrom(),
                battleResult.getFinalAttackerArmy(), plunderedResource,
                new Officers()
        );
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

    @Getter
    @AllArgsConstructor
    public static class DefenderVillage {
        private Resource resource;
        private Integer wallLevel;
        private Integer loyalty;
    }

}
