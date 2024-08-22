package pl.app.god_equipment.adapter.out;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import pl.app.god_equipment.application.domain.GodEquipment;
import pl.app.god_equipment.application.domain.GodEquipmentException;
import pl.app.god_equipment.application.port.out.GodEquipmentDomainRepository;
import reactor.core.publisher.Mono;


@Component
@RequiredArgsConstructor
class GodEquipmentDomainRepositoryImpl implements GodEquipmentDomainRepository {
    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public Mono<GodEquipment> fetchByAccountId(ObjectId godId) {
        Query query = Query.query(Criteria.where("godId").is(godId));
        return mongoTemplate.query(GodEquipment.class).matching(query).one()
                .switchIfEmpty(Mono.error(() -> GodEquipmentException.NotFoundItemException.fromAccountId(godId.toHexString())));
    }


}
