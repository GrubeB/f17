package pl.app.resource.village_resource.application.port.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import pl.app.resource.village_resource.application.domain.VillageResource;
import pl.app.resource.village_resource.application.domain.VillageResourceException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
class VillageResourceDomainRepositoryImpl implements VillageResourceDomainRepository {
    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public Mono<VillageResource> fetchByVillageId(ObjectId villageId) {
        return mongoTemplate.query(VillageResource.class)
                .matching(Query.query(Criteria.where("villageId").is(villageId)))
                .one()
                .switchIfEmpty(Mono.error(() -> VillageResourceException.NotFoundVillageResourceException.fromId(villageId.toHexString())));
    }

    @Override
    public Flux<VillageResource> fetchAll() {
        return mongoTemplate.query(VillageResource.class)
                .all();
    }
}
