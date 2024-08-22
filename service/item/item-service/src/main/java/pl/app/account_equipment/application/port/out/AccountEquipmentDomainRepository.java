package pl.app.account_equipment.application.port.out;

import org.bson.types.ObjectId;
import pl.app.account_equipment.application.domain.AccountEquipment;
import pl.app.item_template.application.domain.OutfitTemplate;
import pl.app.item_template.application.domain.WeaponTemplate;
import reactor.core.publisher.Mono;

public interface AccountEquipmentDomainRepository {
    Mono<AccountEquipment> fetchByAccountId(ObjectId accountId);
}
