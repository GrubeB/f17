package pl.app.character.http;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import pl.app.common.shared.config.ResponsePage;
import pl.app.monster.application.domain.MonsterException;
import pl.app.monster.query.dto.MonsterDto;
import pl.app.monster.query.dto.MonsterWithGearDto;
import reactor.core.publisher.Mono;

import java.util.List;

@HttpExchange(
        url = "monster-with-gears",
        accept = MediaType.APPLICATION_JSON_VALUE,
        contentType = MediaType.APPLICATION_JSON_VALUE
)
public interface MonsterWithGearDtoQueryControllerHttpInterface {
    @GetExchange("/{id}")
    Mono<ResponseEntity<MonsterWithGearDto>> fetchById(
            @PathVariable ObjectId id
    );

    @GetExchange
    Mono<ResponseEntity<ResponsePage<MonsterWithGearDto>>> fetchAllByIds(
            Pageable pageable,
            @RequestParam(required = false) List<ObjectId> ids
    );

}
