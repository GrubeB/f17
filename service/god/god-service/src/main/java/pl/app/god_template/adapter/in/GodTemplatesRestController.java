package pl.app.god_template.adapter.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.app.god_template.application.port.in.GodTemplateCommand;
import pl.app.god_template.application.port.in.GodTemplateService;
import pl.app.god_template.query.GodTemplateQueryService;
import pl.app.god_template.query.dto.GodTemplateDto;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(GodTemplatesRestController.resourcePath)
@RequiredArgsConstructor
class GodTemplatesRestController {
    public static final String resourceName = "god-templates";
    public static final String resourcePath = "/api/v1/" + resourceName;

    private final GodTemplateService service;
    private final GodTemplateQueryService queryService;


    @PostMapping
    Mono<ResponseEntity<GodTemplateDto>> create(@RequestBody GodTemplateCommand.CreateGodTemplateCommand command) {
        return service.create(command)
                .flatMap(domain -> queryService.fetchById(domain.getId()))
                .map(ResponseEntity::ok);
    }

    @PutMapping("/{id}")
    Mono<ResponseEntity<GodTemplateDto>> update(@PathVariable ObjectId id,
                                                @RequestBody GodTemplateCommand.UpdateGodTemplateCommand command) {
        return service.update(command)
                .flatMap(domain -> queryService.fetchById(domain.getId()))
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    Mono<ResponseEntity<GodTemplateDto>> delete(@PathVariable ObjectId id) {
        return service.delete(new GodTemplateCommand.DeleteGodTemplateCommand(id))
                .flatMap(domain -> queryService.fetchById(domain.getId()))
                .map(ResponseEntity::ok);
    }
}
