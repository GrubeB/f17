package pl.app.monster_gear.adapter.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.app.monster_gear.application.port.in.MonsterGearCommand;
import pl.app.monster_gear.application.port.in.MonsterGearService;
import pl.app.monster_gear.dto.MonsterGearDto;
import pl.app.monster_gear.query.MonsterGearDtoQueryService;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(MonsterGearRestController.resourcePath)
@RequiredArgsConstructor
class MonsterGearRestController {
    public static final String resourceName = "monster-gears";
    public static final String resourcePath = "/api/v1/" + resourceName;

    private final MonsterGearService service;
    private final MonsterGearDtoQueryService queryService;


    @PutMapping("/{monsterId}/items")
    public Mono<ResponseEntity<MonsterGearDto>> setMonsterItem(@PathVariable ObjectId monsterId,
                                                               @RequestBody MonsterGearCommand.SetMonsterItemCommand command) {
        return service.setMonsterItem(command)
                .flatMap(domain -> queryService.fetchByMonsterId(domain.getMonsterId()))
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{monsterId}/items")
    public Mono<ResponseEntity<MonsterGearDto>> removeMonsterItem(@PathVariable ObjectId monsterId,
                                                                  @RequestBody MonsterGearCommand.RemoveMonsterItemCommand command) {

        return service.removeMonsterItem(command)
                .flatMap(domain -> queryService.fetchByMonsterId(domain.getMonsterId()))
                .map(ResponseEntity::ok);
    }
}
