package pl.app.character.http;

import org.bson.types.ObjectId;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import pl.app.family.application.port.in.FamilyCommand;
import pl.app.family.query.dto.FamilyDto;
import reactor.core.publisher.Mono;

@HttpExchange(
        url = "god-families",
        accept = MediaType.APPLICATION_JSON_VALUE,
        contentType = MediaType.APPLICATION_JSON_VALUE
)
public interface GodFamilyControllerHttpInterface {
    @PostExchange
    Mono<ResponseEntity<FamilyDto>> create(@RequestBody FamilyCommand.CreateGodFamilyCommand command);

    @PostExchange("/{godId}/characters")
    Mono<ResponseEntity<FamilyDto>> add(@PathVariable ObjectId godId,
                                        @RequestBody FamilyCommand.AddCharacterToGodFamilyCommand command);

    @DeleteExchange("/{godId}/characters")
    Mono<ResponseEntity<FamilyDto>> remove(@PathVariable ObjectId godId,
                                           @RequestBody FamilyCommand.RemoveCharacterFromGodFamilyCommand command);
}
