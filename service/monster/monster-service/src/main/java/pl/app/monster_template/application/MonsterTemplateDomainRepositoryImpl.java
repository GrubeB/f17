package pl.app.monster_template.application;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import pl.app.monster_template.application.domain.MonsterTemplate;
import pl.app.monster_template.application.domain.MonsterTemplateException;
import pl.app.monster_template.application.port.in.MonsterTemplateDomainRepository;
import reactor.core.publisher.Mono;


@Component
@RequiredArgsConstructor
class MonsterTemplateDomainRepositoryImpl implements
        MonsterTemplateDomainRepository {
    private static final Logger logger = LoggerFactory.getLogger(MonsterTemplateDomainRepositoryImpl.class);

    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public Mono<MonsterTemplate> fetchById(ObjectId id) {
        Query query = Query.query(Criteria.where("_id").is(id));
        return mongoTemplate.query(MonsterTemplate.class).matching(query).one()
                .switchIfEmpty(Mono.defer(() -> Mono.error(() -> MonsterTemplateException.NotFoundMonsterTemplateException.fromId(id.toHexString()))))
                .doOnError(e -> logger.error("error occurred while fetching monster template with id: {}: {}", id, e.toString()));
    }

}
