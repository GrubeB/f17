package pl.app.god_equipment.application;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import pl.app.god_equipment.application.domain.CharacterGear;
import pl.app.god_equipment.application.domain.GodEquipment;
import pl.app.god_equipment.application.domain.GodEquipmentException;
import pl.app.god_equipment.application.port.in.GodEquipmentDomainRepository;
import reactor.core.publisher.Mono;


@Component
@RequiredArgsConstructor
class GodEquipmentDomainRepositoryImpl implements GodEquipmentDomainRepository {
    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public Mono<GodEquipment> fetchByGodId(ObjectId godId) {
        Query query = Query.query(Criteria.where("godId").is(godId));
        return mongoTemplate.query(GodEquipment.class).matching(query).one()
                .switchIfEmpty(Mono.error(() -> GodEquipmentException.NotFoundGodEquipmentException.fromGodId(godId.toHexString())));
    }

    @Override
    public Mono<CharacterGear> fetchCharacterGearByCharacterId(ObjectId characterId) {
        Query query = Query.query(Criteria.where("characterId").is(characterId));
        return mongoTemplate.query(CharacterGear.class).matching(query).one()
                .switchIfEmpty(Mono.error(() -> GodEquipmentException.NotFoundCharacterGearException.fromCharacterId(characterId.toHexString())));
    }
}
