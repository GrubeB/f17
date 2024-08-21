package pl.app.item_template.adapter.in;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.app.item_template.application.port.in.ItemTemplateCommand;
import pl.app.item_template.application.port.in.ItemTemplateService;
import pl.app.item_template.query.OutfitTemplateQueryService;
import pl.app.item_template.query.dto.OutfitTemplateDto;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(OutfitTemplateRestController.resourcePath)
@RequiredArgsConstructor
class OutfitTemplateRestController {
    public static final String resourceName = "outfit-templates";
    public static final String resourcePath = "/api/v1/" + resourceName;

    private final ItemTemplateService service;
    private final OutfitTemplateQueryService queryService;


    @PostMapping
    public Mono<ResponseEntity<OutfitTemplateDto>> createOutfitTemplate(@RequestBody ItemTemplateCommand.CreateOutfitTemplateCommand command) {
        return service.createOutfitTemplate(command)
                .flatMap(domain -> queryService.fetchById(domain.getId()))
                .map(ResponseEntity::ok);
    }
}
