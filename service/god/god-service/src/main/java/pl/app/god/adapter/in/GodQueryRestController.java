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
import pl.app.god.query.GodQueryService;
import pl.app.god.query.dto.GodDto;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(GodQueryRestController.resourcePath)
@RequiredArgsConstructor
class GodQueryRestController {
    public static final String resourceName = "gods";
    public static final String resourcePath = "/api/v1/" + resourceName;

    private final GodQueryService queryService;

    @GetMapping
    Mono<ResponseEntity<Page<GodDto>>> fetchAllByPageable(Pageable pageable) {
        return queryService.fetchByPageable(pageable)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/{id}")
    Mono<ResponseEntity<GodDto>> fetchById(@PathVariable ObjectId id) {
        return queryService.fetchById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

}
