package pl.app.item.query;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import pl.app.common.shared.model.ItemType;
import pl.app.item.application.domain.Item;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
class ItemQueryServiceImpl implements ItemQueryService {
    private final ReactiveMongoTemplate mongoTemplate;
    @Override
    public Mono<Item> fetchById(@NonNull ObjectId id) {
        Query query = Query.query(Criteria.where("_id").is(id));
        return mongoTemplate.query(Item.class).matching(query).one();
    }
}
