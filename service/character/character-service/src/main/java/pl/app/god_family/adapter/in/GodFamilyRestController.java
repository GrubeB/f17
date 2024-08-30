package pl.app.god_family.adapter.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.app.god_family.application.port.in.GodFamilyCommand;
import pl.app.god_family.application.port.in.GodFamilyService;
import pl.app.god_family.query.GodFamilyQueryService;
import pl.app.god_family.query.dto.GodFamilyDto;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(GodFamilyRestController.resourcePath)
@RequiredArgsConstructor
class GodFamilyRestController {
    public static final String resourceName = "god-families";
    public static final String resourcePath = "/api/v1/" + resourceName;

    private final GodFamilyService service;
    private final GodFamilyQueryService queryService;

    @PostMapping
    Mono<ResponseEntity<GodFamilyDto>> create(@RequestBody GodFamilyCommand.CreateGodFamilyCommand command) {
        return service.create(command)
                .flatMap(domain -> queryService.fetchByGodId(domain.getGodId()))
                .map(ResponseEntity::ok);
    }

    @PostMapping("/{godId}/characters")
    Mono<ResponseEntity<GodFamilyDto>> add(@PathVariable ObjectId godId,
                                           @RequestBody GodFamilyCommand.AddCharacterToGodFamilyCommand command) {
        return service.add(command)
                .flatMap(domain -> queryService.fetchByGodId(domain.getGodId()))
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{godId}/characters")
    Mono<ResponseEntity<GodFamilyDto>> remove(@PathVariable ObjectId godId,
                                              @RequestBody GodFamilyCommand.RemoveCharacterFromGodFamilyCommand command) {
        return service.remove(command)
                .flatMap(domain -> queryService.fetchByGodId(domain.getGodId()))
                .map(ResponseEntity::ok);
    }
}
