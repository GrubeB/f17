package pl.app.character_template.adapter.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.app.character_template.application.port.in.CharacterTemplateService;
import pl.app.character_template.dto.CharacterTemplateDto;
import pl.app.character_template.in.CharacterCommand;
import pl.app.character_template.query.CharacterTemplateQueryService;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(CharacterTemplatesRestController.resourcePath)
@RequiredArgsConstructor
class CharacterTemplatesRestController {
    public static final String resourceName = "character-templates";
    public static final String resourcePath = "/api/v1/" + resourceName;

    private final CharacterTemplateService service;
    private final CharacterTemplateQueryService queryService;


    @PostMapping
    Mono<ResponseEntity<CharacterTemplateDto>> create(@RequestBody CharacterCommand.CreateCharacterTemplateCommand command) {
        return service.create(command)
                .flatMap(domain -> queryService.fetchById(domain.getId()))
                .map(ResponseEntity::ok);
    }

    @PutMapping("/{id}")
    Mono<ResponseEntity<CharacterTemplateDto>> update(@PathVariable ObjectId id,
                                                      @RequestBody CharacterCommand.UpdateCharacterTemplateCommand command) {
        return service.update(command)
                .flatMap(domain -> queryService.fetchById(domain.getId()))
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    Mono<ResponseEntity<CharacterTemplateDto>> delete(@PathVariable ObjectId id) {
        return service.delete(new CharacterCommand.DeleteCharacterTemplateCommand(id))
                .flatMap(domain -> queryService.fetchById(domain.getId()))
                .map(ResponseEntity::ok);
    }
}
