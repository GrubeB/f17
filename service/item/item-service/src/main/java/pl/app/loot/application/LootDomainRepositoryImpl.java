package pl.app.loot.application;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import pl.app.loot.aplication.domain.Loot;
import pl.app.loot.aplication.domain.LootException;
import pl.app.loot.application.port.in.LootDomainRepository;
import reactor.core.publisher.Mono;


@Component
@RequiredArgsConstructor
class LootDomainRepositoryImpl implements LootDomainRepository {
    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public Mono<Loot> fetchByDomainObject(ObjectId domainObjectId, Loot.LootDomainObjectType domainObjectType) {
        Query query = Query.query(Criteria.where("domainObjectId").is(domainObjectId).and("domainObjectType").is(domainObjectType));
        return mongoTemplate.query(Loot.class).matching(query).one()
                .switchIfEmpty(Mono.error(() -> LootException.NotFoundLootException.fromId(domainObjectId.toHexString())));
    }
}
