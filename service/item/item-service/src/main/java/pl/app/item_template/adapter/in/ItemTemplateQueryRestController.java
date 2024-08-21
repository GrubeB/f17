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
import pl.app.item_template.query.ItemTemplateQueryService;
import pl.app.item_template.query.dto.ItemTemplateDto;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(ItemTemplateQueryRestController.resourcePath)
@RequiredArgsConstructor
class ItemTemplateQueryRestController {
    public static final String resourceName = "item-templates";
    public static final String resourcePath = "/api/v1/" + resourceName;

    private final ItemTemplateQueryService queryService;

    @GetMapping
    Mono<ResponseEntity<Page<ItemTemplateDto>>> fetchAllByPageable(Pageable pageable) {
        return queryService.fetchByPageable(pageable)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/{id}")
    Mono<ResponseEntity<ItemTemplateDto>> fetchById(@PathVariable ObjectId id) {
        return queryService.fetchById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

}
