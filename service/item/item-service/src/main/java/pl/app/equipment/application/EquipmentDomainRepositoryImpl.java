package pl.app.equipment.application;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import pl.app.equipment.application.domain.Equipment;
import pl.app.equipment.application.domain.EquipmentException;
import pl.app.equipment.application.port.in.EquipmentDomainRepository;
import reactor.core.publisher.Mono;


@Component
@RequiredArgsConstructor
class EquipmentDomainRepositoryImpl implements EquipmentDomainRepository {
    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public Mono<Equipment> fetchByGodId(ObjectId godId) {
        Query query = Query.query(Criteria.where("godId").is(godId));
        return mongoTemplate.query(Equipment.class).matching(query).one()
                .switchIfEmpty(Mono.error(() -> EquipmentException.NotFoundGodEquipmentException.fromGodId(godId.toHexString())));
    }
}
