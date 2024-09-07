package pl.app.tower.adapter.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.app.monster_template.dto.MonsterTemplateDto;
import pl.app.monster_template.in.MonsterTemplateCommand;
import pl.app.tower.application.domain.TowerLevel;
import pl.app.tower.application.port.in.TowerService;
import pl.app.tower.dto.TowerLevelDto;
import pl.app.tower.in.TowerCommand;
import pl.app.tower.query.TowerLevelQueryService;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(TowerLevelRestController.resourcePath)
@RequiredArgsConstructor
class TowerLevelRestController {
    public static final String resourceName = "tower-levels";
    public static final String resourcePath = "/api/v1/" + resourceName;

    private final TowerService service;
    private final TowerLevelQueryService queryService;


    @PostMapping
    Mono<ResponseEntity<TowerLevelDto>> create(
            @RequestBody TowerCommand.CreateTowerLevelCommand command
    ) {
        return service.create(command)
                .flatMap(domain -> queryService.fetchByLevel(domain.getLevel()))
                .map(ResponseEntity::ok);
    }

    @PutMapping("/{level}")
    Mono<ResponseEntity<TowerLevelDto>> update(
            @PathVariable Integer level,
            @RequestBody TowerCommand.UpdateTowerLevelCommand command
    ) {
        return service.update(command)
                .flatMap(domain -> queryService.fetchByLevel(domain.getLevel()))
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{level}")
    Mono<ResponseEntity<TowerLevelDto>> remove(
            @PathVariable Integer level
    ) {
        return service.remove(new TowerCommand.RemoveTowerLevelCommand(level))
                .flatMap(domain -> queryService.fetchByLevel(domain.getLevel()))
                .map(ResponseEntity::ok);
    }
}
