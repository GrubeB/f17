package pl.app.item_template.adapter.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.app.item_template.application.port.in.ItemTemplateCommand;
import pl.app.item_template.application.port.in.ItemTemplateService;
import pl.app.item_template.query.WeaponTemplateQueryService;
import pl.app.item_template.query.dto.WeaponTemplateDto;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(WeaponTemplateRestController.resourcePath)
@RequiredArgsConstructor
class WeaponTemplateRestController {
    public static final String resourceName = "weapon-templates";
    public static final String resourcePath = "/api/v1/" + resourceName;

    private final ItemTemplateService service;
    private final WeaponTemplateQueryService queryService;


    @PostMapping
    public Mono<ResponseEntity<WeaponTemplateDto>> createWeaponTemplate(@RequestBody ItemTemplateCommand.CreateWeaponTemplateCommand command) {
        return service.createWeaponTemplate(command)
                .flatMap(domain -> queryService.fetchById(domain.getId()))
                .map(ResponseEntity::ok);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<WeaponTemplateDto>> updateWeaponTemplate(@PathVariable ObjectId id, @RequestBody ItemTemplateCommand.UpdateWeaponTemplateCommand command) {
        command.setId(id);
        return service.updateWeaponTemplate(command)
                .flatMap(domain -> queryService.fetchById(domain.getId()))
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteWeaponTemplate(@PathVariable ObjectId id) {
        return service.deleteWeaponTemplate(new ItemTemplateCommand.DeleteWeaponTemplateCommand(id))
                .map(domain -> ResponseEntity.accepted().build());
    }
}
