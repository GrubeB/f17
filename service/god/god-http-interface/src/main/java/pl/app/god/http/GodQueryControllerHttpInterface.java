package pl.app.god.http;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import pl.app.god.query.dto.GodAggregateDto;
import pl.app.god.query.dto.GodDto;
import reactor.core.publisher.Mono;

@HttpExchange(
        url = "gods",
        accept = MediaType.APPLICATION_JSON_VALUE,
        contentType = MediaType.APPLICATION_JSON_VALUE
)
public interface GodQueryControllerHttpInterface {
    @GetExchange("/{id}")
    Mono<ResponseEntity<GodAggregateDto>> fetchById(@PathVariable ObjectId id);
    @GetExchange
    Mono<ResponseEntity<Page<GodAggregateDto>>> fetchAllByPageable(Pageable pageable);
}
