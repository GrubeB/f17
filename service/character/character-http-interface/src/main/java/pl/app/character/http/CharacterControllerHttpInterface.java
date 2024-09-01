package pl.app.character.http;

import org.bson.types.ObjectId;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.annotation.PutExchange;
import pl.app.character.application.port.in.CharacterCommand;
import pl.app.character.query.dto.CharacterDto;
import reactor.core.publisher.Mono;

@HttpExchange(
        url = "characters",
        accept = MediaType.APPLICATION_JSON_VALUE,
        contentType = MediaType.APPLICATION_JSON_VALUE
)
public interface CharacterControllerHttpInterface {
    @PostExchange
    Mono<ResponseEntity<CharacterDto>> createCharacter(@RequestBody CharacterCommand.CreateCharacterCommand command);

    @PostExchange("/random")
    Mono<ResponseEntity<CharacterDto>> createRandomCharacter(@RequestBody CharacterCommand.CreateRandomCharacterCommand command);

    @PutExchange("/{id}/statistics")
    Mono<ResponseEntity<CharacterDto>> addStatistic(@PathVariable ObjectId id,
                                                    @RequestBody CharacterCommand.AddStatisticCommand command);
}
