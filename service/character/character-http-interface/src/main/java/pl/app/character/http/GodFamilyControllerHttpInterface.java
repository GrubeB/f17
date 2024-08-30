package pl.app.character.http;

import org.bson.types.ObjectId;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import pl.app.god_family.application.port.in.GodFamilyCommand;
import pl.app.god_family.query.dto.GodFamilyDto;
import reactor.core.publisher.Mono;

@HttpExchange(
        url = "god-families",
        accept = MediaType.APPLICATION_JSON_VALUE,
        contentType = MediaType.APPLICATION_JSON_VALUE
)
public interface GodFamilyControllerHttpInterface {
    @PostExchange
    Mono<ResponseEntity<GodFamilyDto>> create(@RequestBody GodFamilyCommand.CreateGodFamilyCommand command);

    @PostExchange("/{godId}/characters")
    Mono<ResponseEntity<GodFamilyDto>> add(@PathVariable ObjectId godId,
                                           @RequestBody GodFamilyCommand.AddCharacterToGodFamilyCommand command);

    @DeleteExchange("/{godId}/characters")
    Mono<ResponseEntity<GodFamilyDto>> remove(@PathVariable ObjectId godId,
                                              @RequestBody GodFamilyCommand.RemoveCharacterFromGodFamilyCommand command);
}
