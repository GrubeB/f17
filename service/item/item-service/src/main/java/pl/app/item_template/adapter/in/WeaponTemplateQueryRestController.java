package pl.app.item_template.adapter.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.app.item_template.application.domain.ItemTemplateException;
import pl.app.item_template.query.WeaponTemplateQueryService;
import pl.app.item_template.query.dto.WeaponTemplateDto;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(WeaponTemplateQueryRestController.resourcePath)
@RequiredArgsConstructor
class WeaponTemplateQueryRestController {
    public static final String resourceName = "weapon-templates";
    public static final String resourcePath = "/api/v1/" + resourceName;

    private final WeaponTemplateQueryService queryService;

    @GetMapping("/{id}")
    Mono<ResponseEntity<WeaponTemplateDto>> fetchById(
            @PathVariable ObjectId id
    ) {
        return queryService.fetchById(id)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.error(ItemTemplateException.NotFoundItemTemplateException.fromId(id.toHexString())));
    }

    @GetMapping
    Mono<ResponseEntity<Page<WeaponTemplateDto>>> fetchAllByPageable(
            Pageable pageable
    ) {
        return queryService.fetchAllByPageable(pageable)
                .map(ResponseEntity::ok);
    }
}
