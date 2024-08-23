package pl.app.account.http;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import pl.app.account.query.dto.AccountDto;
import reactor.core.publisher.Mono;

@HttpExchange(
        url = "accounts",
        accept = MediaType.APPLICATION_JSON_VALUE,
        contentType = MediaType.APPLICATION_JSON_VALUE
)
public interface AccountQueryControllerHttpInterface {
    @GetExchange
    Mono<ResponseEntity<Page<AccountDto>>> fetchAllByPageable(Pageable pageable);

    @GetExchange("/{id}")
    Mono<ResponseEntity<AccountDto>> fetchById(@PathVariable ObjectId id);
}