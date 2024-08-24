package pl.app.battle.adapter.out;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import pl.app.battle.application.domain.BattleCharacter;
import pl.app.battle.application.domain.BattleCharacterType;
import pl.app.battle.application.port.out.CharacterRepository;
import pl.app.character.http.CharacterWithGearQueryControllerHttpInterface;
import pl.app.character.http.GodFamilyWithGearQueryControllerHttpInterface;
import pl.app.character.query.dto.CharacterWithGearDto;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
class CharacterRepositoryImpl implements CharacterRepository {
    private final CharacterWithGearQueryControllerHttpInterface characterWithGearQueryControllerHttpInterface;
    private final GodFamilyWithGearQueryControllerHttpInterface familyWithGearQueryControllerHttpInterface;

    @Override
    public Mono<BattleCharacter> getBattleCharacterById(ObjectId characterId) {
        return characterWithGearQueryControllerHttpInterface.fetchById(characterId)
                .map(HttpEntity::getBody)
                .map(dto -> mapToBattleCharacter(dto, null));
    }

    @Override
    public Mono<Set<BattleCharacter>> getBattleCharacterByGodId(ObjectId godId, Set<ObjectId> characterIds) {
        return familyWithGearQueryControllerHttpInterface.fetchByGodId(godId)
                .map(HttpEntity::getBody)
                .map(wrapper ->
                        wrapper.getCharacters().stream()
                                .filter(ch -> characterIds.contains(ch.getId()))
                                .map(dto -> mapToBattleCharacter(dto, godId))
                                .collect(Collectors.toSet())
                );
    }

    private BattleCharacter mapToBattleCharacter(CharacterWithGearDto dto, ObjectId godId) {
        return new BattleCharacter(
                dto.getId(),
                godId,
                BattleCharacterType.PLAYER,
                dto.getProfession(),
                dto.getName(),
                dto.getLevel().getLevel(),
                dto.getLevel().getExp(),
                dto.getBase(),
                dto.getGear(),
                dto.getStatistics(),
                dto.getHp(),
                dto.getDef(),
                dto.getAttackPower(),
                dto.getCharacterGearDto().getLeftHand(),
                dto.getCharacterGearDto().getRightHand()
        );
    }
}
