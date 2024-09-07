package pl.app.monster.application;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import pl.app.monster.application.domain.MonsterException;
import pl.app.monster.application.domain.Monster;
import pl.app.monster.application.port.in.MonsterDomainRepository;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
class MonsterDomainRepositoryImpl implements MonsterDomainRepository {
    private static final Logger logger = LoggerFactory.getLogger(MonsterDomainRepositoryImpl.class);

    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public Mono<Monster> fetchById(ObjectId id) {
        Query query = Query.query(Criteria.where("_id").is(id));
        return mongoTemplate.query(Monster.class).matching(query).one()
                .switchIfEmpty(Mono.defer(() -> Mono.error(() -> MonsterException.NotFoundMonsterException.fromId(id.toString()))))
                .doOnError(e -> logger.error("error occurred while fetching monster with id: {}: {}", id, e.toString()));
    }

    @Override
    public Mono<Set<Monster>> fetchAllById(Set<ObjectId> ids) {
        Query query = Query.query(Criteria.where("_id").in(ids));
        return mongoTemplate.query(Monster.class).matching(query).all().collect(Collectors.toSet());
    }
}
