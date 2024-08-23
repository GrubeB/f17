package pl.app.character.http;

import org.bson.types.ObjectId;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import pl.app.common.shared.config.ResponsePage;
import pl.app.god_family.query.dto.GodFamilyDto;
import reactor.core.publisher.Mono;

import java.util.List;

@HttpExchange(
        url = "god-families",
        accept = MediaType.APPLICATION_JSON_VALUE,
        contentType = MediaType.APPLICATION_JSON_VALUE
)
public interface GodFamilyQueryControllerHttpInterface {
    @GetExchange("/{id}")
    Mono<ResponseEntity<GodFamilyDto>> fetchByGodId(@PathVariable ObjectId id);

    @GetExchange
    Mono<ResponseEntity<ResponsePage<GodFamilyDto>>> fetchAllByGodIds(@RequestParam List<ObjectId> godIds);
}
