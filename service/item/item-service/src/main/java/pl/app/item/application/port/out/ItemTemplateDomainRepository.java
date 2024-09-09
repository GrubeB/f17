package pl.app.item.application.port.out;

import org.bson.types.ObjectId;
import pl.app.common.shared.model.ItemType;
import pl.app.item_template.application.domain.ItemTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;

public interface ItemTemplateDomainRepository {
    Mono<ItemTemplate> fetchTemplateById(ObjectId id);

    Flux<ItemTemplate> fetchRandomTemplate(Set<ItemType> type, Integer numberOfTemplates);
}
