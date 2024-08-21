package pl.app.item_template.adapter.in;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.app.item_template.application.port.in.ItemTemplateCommand;
import pl.app.item_template.application.port.in.ItemTemplateService;
import pl.app.item_template.query.ItemTemplateQueryService;
import pl.app.item_template.query.dto.ItemTemplateDto;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(ItemTemplateRestController.resourcePath)
@RequiredArgsConstructor
class ItemTemplateRestController {
    public static final String resourceName = "item-templates";
    public static final String resourcePath = "/api/v1/" + resourceName;

    private final ItemTemplateService service;
    private final ItemTemplateQueryService queryService;


    @PostMapping
    public Mono<ResponseEntity<ItemTemplateDto>> createCharacter(@RequestBody ItemTemplateCommand.CreateItemTemplateCommand command) {
        return service.createItemTemplate(command)
                .flatMap(domain -> queryService.fetchById(domain.getId()))
                .map(ResponseEntity::ok);
    }
}
