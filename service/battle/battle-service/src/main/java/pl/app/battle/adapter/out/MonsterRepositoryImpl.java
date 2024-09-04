package pl.app.battle.adapter.out;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;
import pl.app.battle.application.domain.BattleCharacter;
import pl.app.battle.application.domain.BattleCharacterType;
import pl.app.battle.application.port.out.MonsterRepository;
import pl.app.common.shared.model.Statistics;
import pl.app.item.query.dto.WeaponDto;
import reactor.core.publisher.Mono;

import java.util.Set;

@Component
class MonsterRepositoryImpl implements MonsterRepository {
    @Override
    public Mono<Set<BattleCharacter>> getByTowerLevel(Integer level) {
        return Mono.just(Set.of(monster1(), monster2(), monster3()));
    }

    private BattleCharacter monster1() {
        return new BattleCharacter(ObjectId.get(), ObjectId.get(), BattleCharacterType.MONSTER, "WARRIOR", "AlaKot2", 10, 2000L,
                new Statistics(40L, 20L, 20L, 19L, 60_000L, 50_000L, 20_000L, 20_000L),
                new Statistics(0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L),
                new Statistics(40L, 20L, 20L, 19L, 60_000L, 50_000L, 20_000L, 20_000L),
                2_000L, 92L, 80L,
                new WeaponDto(2L, 5L),
                null);
    }

    private BattleCharacter monster2() {
        return new BattleCharacter(ObjectId.get(), ObjectId.get(), BattleCharacterType.MONSTER, "WARRIOR", "AlaKot2", 10, 2000L,
                new Statistics(40L, 20L, 20L, 19L, 60_000L, 50_000L, 20_000L, 20_000L),
                new Statistics(0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L),
                new Statistics(40L, 20L, 20L, 19L, 60_000L, 50_000L, 20_000L, 20_000L),
                2_000L, 92L, 80L,
                new WeaponDto(2L, 5L),
                null);
    }

    private BattleCharacter monster3() {
        return new BattleCharacter(ObjectId.get(), ObjectId.get(), BattleCharacterType.MONSTER, "WARRIOR", "AlaKot2", 10, 2000L,
                new Statistics(40L, 20L, 20L, 19L, 60_000L, 50_000L, 20_000L, 20_000L),
                new Statistics(0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L),
                new Statistics(40L, 20L, 20L, 19L, 60_000L, 50_000L, 20_000L, 20_000L),
                2_000L, 92L, 80L,
                new WeaponDto(2L, 5L),
                null);
    }
}
