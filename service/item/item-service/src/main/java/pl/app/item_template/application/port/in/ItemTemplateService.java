package pl.app.item_template.application.port.in;

import pl.app.item_template.application.domain.ItemTemplate;
import pl.app.item_template.application.domain.OutfitTemplate;
import pl.app.item_template.application.domain.WeaponTemplate;
import pl.app.item_template.query.dto.OutfitTemplateDto;
import pl.app.item_template.query.dto.WeaponTemplateDto;
import reactor.core.publisher.Mono;

public interface ItemTemplateService {
    Mono<OutfitTemplate> createOutfitTemplate(ItemTemplateCommand.CreateOutfitTemplateCommand command);
    Mono<WeaponTemplate> createWeaponTemplate(ItemTemplateCommand.CreateWeaponTemplateCommand command);
}
