package pl.app.item.http;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import pl.app.common.shared.config.ResponsePage;
import pl.app.item.query.dto.OutfitDto;
import reactor.core.publisher.Mono;

@HttpExchange(
        url = "outfits",
        accept = MediaType.APPLICATION_JSON_VALUE,
        contentType = MediaType.APPLICATION_JSON_VALUE
)
public interface OutfitQueryControllerHttpInterface {
    @GetExchange("/{id}")
    Mono<ResponseEntity<OutfitDto>> fetchById(@PathVariable ObjectId id);

    @GetExchange
    Mono<ResponseEntity<ResponsePage<OutfitDto>>> fetchAllByPageable(Pageable pageable);

}
