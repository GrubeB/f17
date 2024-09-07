package pl.app.monster_template.adapter.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.app.monster_template.application.port.in.MonsterTemplateService;
import pl.app.monster_template.dto.MonsterTemplateDto;
import pl.app.monster_template.in.MonsterTemplateCommand;
import pl.app.monster_template.query.MonsterTemplateQueryService;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(MonsterTemplatesRestController.resourcePath)
@RequiredArgsConstructor
class MonsterTemplatesRestController {
    public static final String resourceName = "monster-templates";
    public static final String resourcePath = "/api/v1/" + resourceName;

    private final MonsterTemplateService service;
    private final MonsterTemplateQueryService queryService;


    @PostMapping
    Mono<ResponseEntity<MonsterTemplateDto>> create(
            @RequestBody MonsterTemplateCommand.CreateMonsterTemplateCommand command) {
        return service.create(command)
                .flatMap(domain -> queryService.fetchById(domain.getId()))
                .map(ResponseEntity::ok);
    }

    @PutMapping("/{id}")
    Mono<ResponseEntity<MonsterTemplateDto>> update(
            @PathVariable ObjectId id,
            @RequestBody MonsterTemplateCommand.UpdateMonsterTemplateCommand command) {
        return service.update(command)
                .flatMap(domain -> queryService.fetchById(domain.getId()))
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    Mono<ResponseEntity<MonsterTemplateDto>> remove(
            @PathVariable ObjectId id) {
        return service.remove(new MonsterTemplateCommand.RemoveMonsterTemplateCommand(id))
                .flatMap(domain -> queryService.fetchById(domain.getId()))
                .map(ResponseEntity::ok);
    }
}
