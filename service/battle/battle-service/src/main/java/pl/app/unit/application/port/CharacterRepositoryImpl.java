package pl.app.unit.application.port;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import pl.app.unit.application.domain.BattleCharacter;
import pl.app.unit.application.domain.BattleUnitType;
import pl.app.character.http.CharacterWithGearQueryControllerHttpInterface;
import pl.app.character.http.GodFamilyWithGearQueryControllerHttpInterface;
import pl.app.character.query.dto.CharacterWithGearDto;
import pl.app.unit.application.port.in.CharacterRepository;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
class CharacterRepositoryImpl implements
        CharacterRepository
{
    private final CharacterWithGearQueryControllerHttpInterface characterWithGearQueryControllerHttpInterface;
    private final GodFamilyWithGearQueryControllerHttpInterface familyWithGearQueryControllerHttpInterface;

    @Override
    public Mono<BattleCharacter> getBattleCharacterById(ObjectId characterId) {
        return characterWithGearQueryControllerHttpInterface.fetchById(characterId)
                .map(HttpEntity::getBody)
                .map(dto -> new BattleCharacter(dto, null));
    }

    @Override
    public Mono<Set<BattleCharacter>> getBattleCharacterByGodId(ObjectId godId, Set<ObjectId> characterIds) {
        return familyWithGearQueryControllerHttpInterface.fetchByGodId(godId)
                .map(HttpEntity::getBody)
                .map(wrapper ->
                        wrapper.getCharacters().stream()
                                .filter(ch -> characterIds.contains(ch.getId()))
                                .map(dto ->  new BattleCharacter(dto, godId))
                                .collect(Collectors.toSet())
                );
    }
}
