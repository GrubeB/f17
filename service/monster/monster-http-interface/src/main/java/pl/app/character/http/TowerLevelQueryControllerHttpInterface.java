package pl.app.character.http;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import pl.app.common.shared.config.ResponsePage;
import pl.app.tower.dto.TowerLevelDto;
import reactor.core.publisher.Mono;

@HttpExchange(
        url = "tower-levels",
        accept = MediaType.APPLICATION_JSON_VALUE,
        contentType = MediaType.APPLICATION_JSON_VALUE
)
public interface TowerLevelQueryControllerHttpInterface {
    @GetExchange("/{level}")
    Mono<ResponseEntity<TowerLevelDto>> fetchByLevel(
            @PathVariable Integer level
    );

    @GetExchange
    Mono<ResponseEntity<ResponsePage<TowerLevelDto>>> fetchAll();
}
