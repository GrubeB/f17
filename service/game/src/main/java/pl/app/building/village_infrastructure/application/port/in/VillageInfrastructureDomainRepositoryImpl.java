package pl.app.building.village_infrastructure.application.port.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import pl.app.building.village_infrastructure.application.domain.VillageInfrastructure;
import pl.app.building.village_infrastructure.application.domain.VillageInfrastructureException;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
class VillageInfrastructureDomainRepositoryImpl implements VillageInfrastructureDomainRepository {
    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public Mono<VillageInfrastructure> fetchByVillageId(ObjectId villageId) {
        return mongoTemplate.query(VillageInfrastructure.class)
                .matching(Query.query(Criteria.where("villageId").is(villageId)))
                .one()
                .switchIfEmpty(Mono.error(() -> VillageInfrastructureException.NotFoundVillageInfrastructureException.fromId(villageId.toHexString())));
    }
}
