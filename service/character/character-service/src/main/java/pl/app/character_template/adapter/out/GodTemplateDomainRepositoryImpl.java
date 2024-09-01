package pl.app.character_template.adapter.out;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import pl.app.character_template.application.domain.CharacterTemplate;
import pl.app.character_template.application.domain.CharacterTemplateException;
import pl.app.character_template.application.port.out.CharacterTemplateDomainRepository;
import pl.app.common.shared.model.ItemType;
import pl.app.item_template.application.domain.ItemTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;


@Component
@RequiredArgsConstructor
class GodTemplateDomainRepositoryImpl implements
        pl.app.character.application.port.out.CharacterTemplateRepository,
        CharacterTemplateDomainRepository {
    private static final Logger logger = LoggerFactory.getLogger(GodTemplateDomainRepositoryImpl.class);

    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public Mono<CharacterTemplate> fetchById(ObjectId id) {
        Query query = Query.query(Criteria.where("_id").is(id));
        return mongoTemplate.query(CharacterTemplate.class).matching(query).one()
                .switchIfEmpty(Mono.defer(() -> Mono.error(() -> CharacterTemplateException.NotFoundCharacterTemplateException.fromId(id.toHexString()))))
                .doOnError(e -> logger.error("error occurred while fetching character template with id: {}: {}", id, e.toString()));
    }

    @Override
    public Mono<CharacterTemplate> fetchRandomTemplate() {
        var sampleOperation = Aggregation.sample(1);
        Aggregation aggregation = Aggregation.newAggregation(sampleOperation);
        return mongoTemplate.aggregate(aggregation, "character_templates", CharacterTemplate.class).single();
    }
}
