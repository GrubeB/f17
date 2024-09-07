package pl.app.monster.adapter.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.app.monster.application.port.in.MonsterService;
import pl.app.monster.application.port.in.MonsterCommand;
import pl.app.monster.query.MonsterQueryService;
import pl.app.monster.query.dto.MonsterDto;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(MonsterRestController.resourcePath)
@RequiredArgsConstructor
class MonsterRestController {
    public static final String resourceName = "monsters";
    public static final String resourcePath = "/api/v1/" + resourceName;

    private final MonsterService service;
    private final MonsterQueryService queryService;
    @PostMapping
    Mono<ResponseEntity<MonsterDto>> create(
            @RequestBody MonsterCommand.CreateMonsterCommand command
    ) {
        return service.create(command)
                .flatMap(domain -> queryService.fetchById(domain.getId()))
                .map(ResponseEntity::ok);
    }
    @DeleteMapping("/{id}")
    Mono<ResponseEntity<Void>> remove(
            @PathVariable ObjectId id
    ) {
        return service.remove(new MonsterCommand.RemoveMonsterCommand(id))
                .map(unused -> ResponseEntity.accepted().build());
    }
}
