package pl.app.item_template.application.port.in;

import pl.app.item_template.application.domain.OutfitTemplate;
import pl.app.item_template.application.domain.WeaponTemplate;
import reactor.core.publisher.Mono;

public interface ItemTemplateService {
    Mono<OutfitTemplate> createOutfitTemplate(ItemTemplateCommand.CreateOutfitTemplateCommand command);

    Mono<WeaponTemplate> createWeaponTemplate(ItemTemplateCommand.CreateWeaponTemplateCommand command);
}
