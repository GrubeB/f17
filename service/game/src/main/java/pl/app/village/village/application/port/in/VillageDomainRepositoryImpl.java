package pl.app.village.village.application.port.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import pl.app.resource.village_resource.application.domain.VillageResource;
import pl.app.resource.village_resource.application.domain.VillageResourceException;
import pl.app.resource.village_resource.application.port.in.VillageResourceDomainRepository;
import pl.app.village.village.application.domain.Village;
import pl.app.village.village.application.domain.VillageException;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
class VillageDomainRepositoryImpl implements VillageDomainRepository {
    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public Mono<Village> fetchById(ObjectId villageId) {
        return mongoTemplate.query(Village.class)
                .matching(Query.query(Criteria.where("id").is(villageId)))
                .one()
                .switchIfEmpty(Mono.error(() -> VillageException.NotFoundVillageException.fromId(villageId.toHexString())));
    }
}
