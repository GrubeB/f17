package pl.app.family.adapter.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.app.family.application.port.in.FamilyCommand;
import pl.app.family.application.port.in.FamilyService;
import pl.app.family.query.FamilyQueryService;
import pl.app.family.query.dto.FamilyDto;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(FamilyRestController.resourcePath)
@RequiredArgsConstructor
class FamilyRestController {
    public static final String resourceName = "god-families";
    public static final String resourcePath = "/api/v1/" + resourceName;

    private final FamilyService service;
    private final FamilyQueryService queryService;

    @PostMapping
    Mono<ResponseEntity<FamilyDto>> create(@RequestBody FamilyCommand.CreateGodFamilyCommand command) {
        return service.create(command)
                .flatMap(domain -> queryService.fetchByGodId(domain.getGodId()))
                .map(ResponseEntity::ok);
    }

    @PostMapping("/{godId}/characters")
    Mono<ResponseEntity<FamilyDto>> add(@PathVariable ObjectId godId,
                                        @RequestBody FamilyCommand.AddCharacterToGodFamilyCommand command) {
        return service.add(command)
                .flatMap(domain -> queryService.fetchByGodId(domain.getGodId()))
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{godId}/characters")
    Mono<ResponseEntity<FamilyDto>> remove(@PathVariable ObjectId godId,
                                           @RequestBody FamilyCommand.RemoveCharacterFromGodFamilyCommand command) {
        return service.remove(command)
                .flatMap(domain -> queryService.fetchByGodId(domain.getGodId()))
                .map(ResponseEntity::ok);
    }
}
