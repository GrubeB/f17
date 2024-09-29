package pl.app.energy.application;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import pl.app.energy.application.domain.Energy;
import pl.app.energy.application.domain.EnergyException;
import pl.app.energy.application.port.in.EnergyDomainRepository;
import reactor.core.publisher.Mono;


@Component
@RequiredArgsConstructor
class EnergyDomainDomainRepositoryImpl implements EnergyDomainRepository {
    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public Mono<Energy> fetchByGodId(ObjectId godId) {
        Query query = Query.query(Criteria.where("godId").is(godId));
        return mongoTemplate.query(Energy.class).matching(query).one()
                .switchIfEmpty(Mono.defer(() ->
                        Mono.error(() -> EnergyException.NotFoundEnergyException.fromGodId(godId.toHexString()))
                ));
    }

}
