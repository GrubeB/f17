package pl.app.map.map.query;

import pl.app.map.map.query.dto.MapDto;
import reactor.core.publisher.Mono;

public interface MapDtoQueryService {
    Mono<MapDto> fetch();
}
