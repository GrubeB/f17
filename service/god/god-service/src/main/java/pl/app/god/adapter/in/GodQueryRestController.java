package pl.app.god.adapter.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.app.god.application.domain.GodException;
import pl.app.god.query.GodAggregateQueryService;
import pl.app.god.query.GodQueryService;
import pl.app.god.query.dto.GodAggregateDto;
import pl.app.god.query.dto.GodDto;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(GodQueryRestController.resourcePath)
@RequiredArgsConstructor
class GodQueryRestController {
    public static final String resourceName = "gods";
    public static final String resourcePath = "/api/v1/" + resourceName;

    private final GodAggregateQueryService queryService;

    @GetMapping("/{id}")
    Mono<ResponseEntity<GodAggregateDto>> fetchById(@PathVariable ObjectId id) {
        return queryService.fetchById(id)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.error(GodException.NotFoundGodException.fromId(id.toHexString())));
    }

    @GetMapping
    Mono<ResponseEntity<Page<GodAggregateDto>>> fetchAllByPageable(Pageable pageable) {
        return queryService.fetchAllByPageable(pageable)
                .map(ResponseEntity::ok);
    }

}
