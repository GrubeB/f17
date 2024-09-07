package pl.app.item_template.application;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import pl.app.common.shared.model.ItemType;
import pl.app.item_template.application.domain.ItemTemplate;
import pl.app.item_template.application.domain.ItemTemplateException;
import pl.app.item_template.application.domain.OutfitTemplate;
import pl.app.item_template.application.domain.WeaponTemplate;
import pl.app.item_template.application.port.in.ItemTemplateDomainRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;


@Component
@RequiredArgsConstructor
class ItemTemplateDomainRepositoryImpl implements
        ItemTemplateDomainRepository,
        pl.app.item.application.port.out.ItemTemplateDomainRepository {
    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public Mono<OutfitTemplate> fetchOutfitTemplateById(ObjectId id) {
        Query query = Query.query(Criteria.where("id").is(id));
        return mongoTemplate.query(OutfitTemplate.class).matching(query).one()
                .switchIfEmpty(Mono.error(() -> ItemTemplateException.NotFoundItemTemplateException.fromId(id.toString())));
    }

    @Override
    public Mono<WeaponTemplate> fetchWeaponTemplateById(ObjectId id) {
        Query query = Query.query(Criteria.where("id").is(id));
        return mongoTemplate.query(WeaponTemplate.class).matching(query).one()
                .switchIfEmpty(Mono.error(() -> ItemTemplateException.NotFoundItemTemplateException.fromId(id.toString())));
    }

    @Override
    public Mono<ItemTemplate> fetchTemplateById(ObjectId id) {
        Query query = Query.query(Criteria.where("id").is(id));
        return mongoTemplate.query(ItemTemplate.class).matching(query).one()
                .switchIfEmpty(Mono.error(() -> ItemTemplateException.NotFoundItemTemplateException.fromId(id.toString())));
    }

    @Override
    public Flux<ItemTemplate> fetchRandomTemplate(Set<ItemType> type, Integer numberOfTemplates) {
        var matchOperation = Aggregation.match(Criteria.where("type").in(type));
        var sampleOperation = Aggregation.sample(numberOfTemplates);
        Aggregation aggregation = Aggregation.newAggregation(matchOperation, sampleOperation);
        return mongoTemplate.aggregate(aggregation, "templates", ItemTemplate.class);
    }
}
