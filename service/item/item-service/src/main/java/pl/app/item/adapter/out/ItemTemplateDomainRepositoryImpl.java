package pl.app.item.adapter.out;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import pl.app.item.application.port.out.ItemTemplateDomainRepository;
import pl.app.item_template.application.domain.ItemTemplateException;
import pl.app.item_template.application.domain.OutfitTemplate;
import pl.app.item_template.application.domain.WeaponTemplate;
import reactor.core.publisher.Mono;


@Component
@RequiredArgsConstructor
class ItemTemplateDomainRepositoryImpl implements ItemTemplateDomainRepository {
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

}
