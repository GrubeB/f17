package pl.app.character.http;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import pl.app.character.query.dto.CharacterDto;
import pl.app.common.shared.config.ResponsePage;
import reactor.core.publisher.Mono;

@HttpExchange(
        url = "characters",
        accept = MediaType.APPLICATION_JSON_VALUE,
        contentType = MediaType.APPLICATION_JSON_VALUE
)
public interface CharacterQueryControllerHttpInterface {
    @GetExchange("/{id}")
    Mono<ResponseEntity<CharacterDto>> fetchById(@PathVariable ObjectId id);
    @GetExchange
    Mono<ResponseEntity<ResponsePage<CharacterDto>>> fetchAllByPageable(Pageable pageable);

}
