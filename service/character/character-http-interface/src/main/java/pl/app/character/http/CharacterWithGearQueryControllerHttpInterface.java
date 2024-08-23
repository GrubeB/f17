package pl.app.character.http;

import org.bson.types.ObjectId;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import pl.app.character.query.dto.CharacterWithGearDto;
import pl.app.common.shared.config.ResponsePage;
import reactor.core.publisher.Mono;

import java.util.List;

@HttpExchange(
        url = "character-with-gears",
        accept = MediaType.APPLICATION_JSON_VALUE,
        contentType = MediaType.APPLICATION_JSON_VALUE
)
public interface CharacterWithGearQueryControllerHttpInterface {
    @GetExchange("/{id}")
    Mono<ResponseEntity<CharacterWithGearDto>> fetchById(@PathVariable ObjectId id);

    @GetExchange
    Mono<ResponseEntity<ResponsePage<CharacterWithGearDto>>> fetchAllByIds(@RequestParam List<ObjectId> ids);
}
