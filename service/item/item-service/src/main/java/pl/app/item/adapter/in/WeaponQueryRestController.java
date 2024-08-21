package pl.app.item.adapter.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.app.item.query.WeaponQueryService;
import pl.app.item.query.dto.WeaponDto;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(WeaponQueryRestController.resourcePath)
@RequiredArgsConstructor
class WeaponQueryRestController {
    public static final String resourceName = "weapons";
    public static final String resourcePath = "/api/v1/" + resourceName;

    private final WeaponQueryService queryService;

    @GetMapping
    Mono<ResponseEntity<Page<WeaponDto>>> fetchAllByPageable(Pageable pageable) {
        return queryService.fetchByPageable(pageable)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/{id}")
    Mono<ResponseEntity<WeaponDto>> fetchById(@PathVariable ObjectId id) {
        return queryService.fetchById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

}
