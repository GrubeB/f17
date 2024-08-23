package pl.app.account.http;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import pl.app.common.shared.config.ResponsePage;
import pl.app.god.query.dto.GodDto;
import reactor.core.publisher.Mono;

@HttpExchange(
        url = "gods",
        accept = MediaType.APPLICATION_JSON_VALUE,
        contentType = MediaType.APPLICATION_JSON_VALUE
)
public interface GodQueryControllerHttpInterface {
    @GetExchange
    Mono<ResponseEntity<ResponsePage<GodDto>>> fetchAllByPageable(Pageable pageable);

    @GetExchange("/{id}")
    Mono<ResponseEntity<GodDto>> fetchById(@PathVariable ObjectId id);
}
