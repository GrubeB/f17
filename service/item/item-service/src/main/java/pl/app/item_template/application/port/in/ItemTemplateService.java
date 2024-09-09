package pl.app.item_template.application.port.in;

import pl.app.item_template.application.domain.OutfitTemplate;
import pl.app.item_template.application.domain.WeaponTemplate;
import reactor.core.publisher.Mono;

public interface ItemTemplateService {
    Mono<OutfitTemplate> createOutfitTemplate(ItemTemplateCommand.CreateOutfitTemplateCommand command);

    Mono<OutfitTemplate> updateOutfitTemplate(ItemTemplateCommand.UpdateOutfitTemplateCommand command);

    Mono<OutfitTemplate> deleteOutfitTemplate(ItemTemplateCommand.DeleteOutfitTemplateCommand command);

    Mono<WeaponTemplate> createWeaponTemplate(ItemTemplateCommand.CreateWeaponTemplateCommand command);

    Mono<WeaponTemplate> updateWeaponTemplate(ItemTemplateCommand.UpdateWeaponTemplateCommand command);

    Mono<WeaponTemplate> deleteWeaponTemplate(ItemTemplateCommand.DeleteWeaponTemplateCommand command);
}
