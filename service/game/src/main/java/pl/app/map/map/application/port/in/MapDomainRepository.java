package pl.app.map.map.application.port.in;

import pl.app.map.map.application.domain.Map;
import reactor.core.publisher.Mono;

public interface MapDomainRepository {
    Mono<Map> fetch();
}
