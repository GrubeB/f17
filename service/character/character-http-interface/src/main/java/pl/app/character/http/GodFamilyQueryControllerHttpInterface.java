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
import pl.app.god_family.query.dto.GodFamilyDto;
import reactor.core.publisher.Mono;

@HttpExchange(
        url = "god-families",
        accept = MediaType.APPLICATION_JSON_VALUE,
        contentType = MediaType.APPLICATION_JSON_VALUE
)
public interface GodFamilyQueryControllerHttpInterface {
    @GetExchange
    Mono<ResponseEntity<Page<GodFamilyDto>>> fetchAllByPageable(Pageable pageable);

    @GetExchange("/{id}")
    Mono<ResponseEntity<GodFamilyDto>> fetchById(@PathVariable ObjectId id);
}
