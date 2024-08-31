package pl.app.god.application.port.out;

import org.bson.types.ObjectId;
import pl.app.god.application.domain.God;
import pl.app.god_template.application.domain.GodTemplate;
import reactor.core.publisher.Mono;

public interface GodTemplateRepository {
    Mono<GodTemplate> fetchById(ObjectId id);
}
