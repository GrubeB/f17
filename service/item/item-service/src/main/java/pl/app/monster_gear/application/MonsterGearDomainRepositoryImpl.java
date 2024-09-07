package pl.app.monster_gear.application;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import pl.app.monster_gear.application.domain.MonsterGear;
import pl.app.monster_gear.application.domain.MonsterGearException;
import pl.app.monster_gear.application.port.in.MonsterGearDomainRepository;
import reactor.core.publisher.Mono;


@Component
@RequiredArgsConstructor
class MonsterGearDomainRepositoryImpl implements MonsterGearDomainRepository {
    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public Mono<MonsterGear> fetchByMonsterId(ObjectId monsterId) {
        Query query = Query.query(Criteria.where("monsterId").is(monsterId));
        return mongoTemplate.query(MonsterGear.class).matching(query).one()
                .switchIfEmpty(Mono.error(() -> MonsterGearException.NotFoundMonsterGearException.fromId(monsterId.toHexString())));
    }

}
