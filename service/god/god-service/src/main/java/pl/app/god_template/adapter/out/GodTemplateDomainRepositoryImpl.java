package pl.app.god_template.adapter.out;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import pl.app.god_template.application.domain.GodTemplate;
import pl.app.god_template.application.domain.GodTemplateException;
import pl.app.god_template.application.port.out.GodTemplateDomainRepository;
import reactor.core.publisher.Mono;

;

@Component
@RequiredArgsConstructor
class GodTemplateDomainRepositoryImpl implements
        GodTemplateDomainRepository,
        pl.app.god.application.port.out.GodTemplateRepository
{
    private static final Logger logger = LoggerFactory.getLogger(GodTemplateDomainRepositoryImpl.class);

    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public Mono<GodTemplate> fetchById(ObjectId id) {
        Query query = Query.query(Criteria.where("_id").is(id));
        return mongoTemplate.query(GodTemplate.class).matching(query).one()
                .switchIfEmpty(Mono.defer(() -> Mono.error(() -> GodTemplateException.NotFoundGodTemplateException.fromId(id.toHexString()))))
                .doOnError(e -> logger.error("error occurred while fetching god template with id: {}: {}", id, e.toString()));
    }
}
