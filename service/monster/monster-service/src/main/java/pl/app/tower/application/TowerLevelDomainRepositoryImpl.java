package pl.app.tower.application;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import pl.app.tower.application.domain.TowerException;
import pl.app.tower.application.domain.TowerLevel;
import pl.app.tower.application.port.in.TowerLevelDomainRepository;
import reactor.core.publisher.Mono;


@Component
@RequiredArgsConstructor
class TowerLevelDomainRepositoryImpl implements
        TowerLevelDomainRepository {
    private static final Logger logger = LoggerFactory.getLogger(TowerLevelDomainRepositoryImpl.class);

    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public Mono<TowerLevel> fetchByLevel(Integer level) {
        Query query = Query.query(Criteria.where("level").is(level));
        return mongoTemplate.query(TowerLevel.class).matching(query).one()
                .switchIfEmpty(Mono.defer(() -> Mono.error(() -> TowerException.NotFoundTowerLevelException.fromLevel(level))))
                .doOnError(e -> logger.error("error occurred while fetching tower level with level: {}: {}", level, e.toString()));
    }

}
