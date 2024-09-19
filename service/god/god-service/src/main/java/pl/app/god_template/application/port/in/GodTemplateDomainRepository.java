package pl.app.god_template.application.port.in;

import org.bson.types.ObjectId;
import pl.app.god_template.application.domain.GodTemplate;
import reactor.core.publisher.Mono;

public interface GodTemplateDomainRepository {
    Mono<GodTemplate> fetchById(ObjectId id);
}
