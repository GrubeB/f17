package pl.app.gear.application;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import pl.app.gear.aplication.domain.Gear;
import pl.app.gear.application.port.in.GearDomainRepository;
import pl.app.loot.aplication.domain.LootException;
import reactor.core.publisher.Mono;


@Component
@RequiredArgsConstructor
class GearDomainRepositoryImpl implements GearDomainRepository {
    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public Mono<Gear> fetchByDomainObject(ObjectId domainObjectId, Gear.LootDomainObjectType domainObjectType) {
        Query query = Query.query(Criteria.where("domainObjectId").is(domainObjectId).and("domainObjectType").is(domainObjectType));
        return mongoTemplate.query(Gear.class).matching(query).one()
                .switchIfEmpty(Mono.error(() -> LootException.NotFoundLootException.fromId(domainObjectId.toHexString())));
    }
}
