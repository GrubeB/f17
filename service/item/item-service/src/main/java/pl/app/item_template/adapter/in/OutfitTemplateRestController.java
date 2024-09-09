package pl.app.item_template.adapter.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
    public Mono<ResponseEntity<OutfitTemplateDto>> createOutfitTemplate(
            @RequestBody ItemTemplateCommand.CreateOutfitTemplateCommand command
    ) {
        return service.createOutfitTemplate(command)
                .flatMap(domain -> queryService.fetchById(domain.getId()))
                .map(ResponseEntity::ok);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<OutfitTemplateDto>> updateOutfitTemplate(
            @PathVariable ObjectId id,
            @RequestBody ItemTemplateCommand.UpdateOutfitTemplateCommand command
    ) {
        command.setId(id);
        return service.updateOutfitTemplate(command)
                .flatMap(domain -> queryService.fetchById(domain.getId()))
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteOutfitTemplate(
            @PathVariable ObjectId id
    ) {
        return service.deleteOutfitTemplate(new ItemTemplateCommand.DeleteOutfitTemplateCommand(id))
                .map(domain -> ResponseEntity.accepted().build());
    }
}
