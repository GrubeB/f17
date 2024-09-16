package pl.app.unit.application.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import pl.app.common.shared.model.CharacterProfession;
import pl.app.common.shared.model.Money;
import pl.app.common.shared.model.Statistics;
import pl.app.item.query.dto.WeaponDto;
import pl.app.item_template.query.dto.ItemTemplateDto;
import pl.app.monster.query.dto.MonsterWithGearDto;

import java.io.Serializable;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class BattleMonster extends BattleUnit {
    private BattleMonsterProgress progress;
    private BattleMonsterLoot loot;

    public BattleMonster(CharacterProfession profession,
                         String name, Integer level, Long exp,
                         Statistics statistics,
                         Long hp, Long def, Long attackPower, WeaponDto leftHand, WeaponDto rightHand,
                         ObjectId monsterId, BattleMonsterProgress progress, BattleMonsterLoot loot) {
        super(monsterId, BattleUnitType.CHARACTER, profession, name, level, exp, statistics, hp, def, attackPower, leftHand, rightHand);
        this.progress = progress;
        this.loot = loot;
    }

    public BattleMonster(MonsterWithGearDto dto) {
        this(dto.getProfession(),
                dto.getName(), dto.getLevel(), 0L,
                dto.getStatistics(),
                dto.getHp(), dto.getDef(), dto.getAttackPower(), dto.getMonsterGear().getLeftHand(), dto.getMonsterGear().getLeftHand(),
                dto.getId(), new BattleMonsterProgress(dto.getProgress().getExp()), new BattleMonsterLoot(dto.getMonsterLoot().getMoney(),
                        dto.getMonsterLoot().getItems().stream().map(e -> new LootItem(e.getItemTemplate().getId(),e.getChance(), e.getAmount())).collect(Collectors.toSet())
                ));
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BattleMonsterProgress {
        private Long exp;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BattleMonsterLoot {
        private Money money;
        private Set<LootItem> items;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LootItem implements Serializable {
        private ObjectId itemTemplateId;
        private Integer chance;
        private Integer amount;
    }
}
