package pl.app.item.adapter.in;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.app.item.application.port.in.ItemCommand;
import pl.app.item.application.port.in.ItemService;
import pl.app.item.query.OutfitQueryService;
import pl.app.item.query.dto.OutfitDto;
import pl.app.item_template.application.port.in.ItemTemplateCommand;
import pl.app.item_template.application.port.in.ItemTemplateService;
import pl.app.item_template.query.OutfitTemplateQueryService;
import pl.app.item_template.query.dto.OutfitTemplateDto;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(OutfitRestController.resourcePath)
@RequiredArgsConstructor
class OutfitRestController {
    public static final String resourceName = "outfits";
    public static final String resourcePath = "/api/v1/" + resourceName;

    private final ItemService service;
    private final OutfitQueryService queryService;


    @PostMapping
    public Mono<ResponseEntity<OutfitDto>> createOutfit(@RequestBody ItemCommand.CreateOutfitCommand command) {
        return service.createOutfit(command)
                .flatMap(domain -> queryService.fetchById(domain.getId()))
                .map(ResponseEntity::ok);
    }
}
