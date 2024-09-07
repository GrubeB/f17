package pl.app.item.application;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import pl.app.item.application.domain.Item;
import pl.app.item.application.port.in.ItemDomainRepository;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
class ItemDomainRepositoryImpl implements ItemDomainRepository {
    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public Mono<Item> fetchById(@NonNull ObjectId id) {
        Query query = Query.query(Criteria.where("_id").is(id));
        return mongoTemplate.query(Item.class).matching(query).one();
    }
}