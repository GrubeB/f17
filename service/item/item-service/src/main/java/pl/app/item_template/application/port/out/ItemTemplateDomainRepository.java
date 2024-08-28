package pl.app.item_template.application.port.out;

import org.bson.types.ObjectId;
import pl.app.item_template.application.domain.OutfitTemplate;
import pl.app.item_template.application.domain.WeaponTemplate;
import reactor.core.publisher.Mono;

public interface ItemTemplateDomainRepository {
    Mono<OutfitTemplate> fetchOutfitTemplateById(ObjectId id);

    Mono<WeaponTemplate> fetchWeaponTemplateById(ObjectId id);
}
