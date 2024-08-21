package pl.app.battle.adapter.out;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import pl.app.battle.application.domain.BattleCharacter;
import pl.app.battle.application.domain.BattleCharacterStatistics;
import pl.app.battle.application.domain.BattleCharacterType;
import pl.app.battle.application.port.out.CharacterRepository;
import pl.app.character.http.CharacterQueryControllerHttpInterface;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
class CharacterRepositoryImpl implements CharacterRepository {
    private final CharacterQueryControllerHttpInterface votingQueryControllerHttpInterface;

    @Override
    public Mono<BattleCharacter> getBattleCharacterById(ObjectId id) {
        return votingQueryControllerHttpInterface.fetchById(id)
                .map(HttpEntity::getBody)
                .map(dto -> new BattleCharacter(
                        dto.getId(),
                        BattleCharacterType.PLAYER,
                        dto.getProfession(),
                        dto.getName(),
                        new BattleCharacterStatistics(
                                dto.getStatistics().getPersistence(),
                                dto.getStatistics().getDurability(),
                                dto.getStatistics().getStrength(),
                                dto.getStatistics().getSpeed(),
                                dto.getStatistics().getCriticalRate(),
                                dto.getStatistics().getCriticalDamage(),
                                dto.getStatistics().getAccuracy(),
                                dto.getStatistics().getResistance()
                        )
                ));
    }
}
