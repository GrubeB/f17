package pl.app.character.adapter.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.app.character.application.port.in.CharacterCommand;
import pl.app.character.application.port.in.CharacterService;
import pl.app.character.query.CharacterQueryService;
import pl.app.character.query.dto.CharacterDto;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(CharacterRestController.resourcePath)
@RequiredArgsConstructor
class CharacterRestController {
    public static final String resourceName = "characters";
    public static final String resourcePath = "/api/v1/" + resourceName;

    private final CharacterService service;
    private final CharacterQueryService queryService;


    @PostMapping
    Mono<ResponseEntity<CharacterDto>> createCharacter(@RequestBody CharacterCommand.CreateCharacterCommand command) {
        return service.createCharacter(command)
                .flatMap(domain -> queryService.fetchById(domain.getId()))
                .map(ResponseEntity::ok);
    }

    @PostMapping("/random")
    Mono<ResponseEntity<CharacterDto>> createRandomCharacter(@RequestBody CharacterCommand.CreateRandomCharacterCommand command) {
        return service.createRandomCharacter(command)
                .flatMap(domain -> queryService.fetchById(domain.getId()))
                .map(ResponseEntity::ok);
    }

    @PutMapping("/{id}/statistics")
    Mono<ResponseEntity<CharacterDto>> addStatistic(@PathVariable ObjectId id,
                                                    @RequestBody CharacterCommand.AddStatisticCommand command) {
        return service.addStatistic(command)
                .flatMap(domain -> queryService.fetchById(domain.getId()))
                .map(ResponseEntity::ok);
    }
}
