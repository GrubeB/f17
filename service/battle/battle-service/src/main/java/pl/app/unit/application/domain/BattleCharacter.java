package pl.app.unit.application.domain;

import lombok.Getter;
import org.bson.types.ObjectId;
import pl.app.character.query.dto.CharacterWithGearDto;
import pl.app.common.shared.model.CharacterProfession;
import pl.app.common.shared.model.Statistics;
import pl.app.item.query.dto.WeaponDto;

@Getter
public class BattleCharacter extends BattleUnit {
    private ObjectId godId;

    public BattleCharacter(CharacterProfession profession,
                           String name, Integer level, Long exp,
                           Statistics statistics,
                           Long hp, Long def, Long attackPower,
                           WeaponDto leftHand, WeaponDto rightHand,
                           ObjectId characterId, ObjectId godId) {
        super(characterId, BattleUnitType.CHARACTER, profession, name, level, exp, statistics, hp, def, attackPower, leftHand, rightHand);
        this.godId = godId;
    }

    public BattleCharacter(CharacterWithGearDto dto, ObjectId godId) {
        this(dto.getProfession(),
                dto.getName(), dto.getLevel().getLevel(), dto.getLevel().getExp(),
                dto.getStatistics(),
                dto.getHp(), dto.getDef(), dto.getAttackPower(), dto.getCharacterGear().getLeftHand(), dto.getCharacterGear().getLeftHand(),
                dto.getId(), godId);
    }
}
