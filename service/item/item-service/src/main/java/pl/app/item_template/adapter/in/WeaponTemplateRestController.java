package pl.app.item_template.adapter.in;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
}
