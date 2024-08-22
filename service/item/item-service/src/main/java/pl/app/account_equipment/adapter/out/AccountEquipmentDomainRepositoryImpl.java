package pl.app.account_equipment.adapter.out;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import pl.app.account_equipment.application.domain.AccountEquipment;
import pl.app.account_equipment.application.domain.AccountEquipmentException;
import pl.app.account_equipment.application.port.out.AccountEquipmentDomainRepository;
import reactor.core.publisher.Mono;


@Component
@RequiredArgsConstructor
class AccountEquipmentDomainRepositoryImpl implements AccountEquipmentDomainRepository {
    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public Mono<AccountEquipment> fetchByAccountId(ObjectId accountId) {
        Query query = Query.query(Criteria.where("accountId").is(accountId));
        return mongoTemplate.query(AccountEquipment.class).matching(query).one()
                .switchIfEmpty(Mono.error(() -> AccountEquipmentException.NotFoundItemException.fromAccountId(accountId.toHexString())));
    }


}
