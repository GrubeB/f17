package pl.app.account.adapter.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.app.account.query.AccountQueryService;
import pl.app.account.query.dto.AccountDto;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(AccountQueryRestController.resourcePath)
@RequiredArgsConstructor
class AccountQueryRestController {
    public static final String resourceName = "accounts";
    public static final String resourcePath = "/api/v1/" + resourceName;

    private final AccountQueryService queryService;

    @GetMapping
    Mono<ResponseEntity<Page<AccountDto>>> fetchAllByPageable(Pageable pageable) {
        return queryService.fetchByPageable(pageable)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/{id}")
    Mono<ResponseEntity<AccountDto>> fetchById(@PathVariable ObjectId id) {
        return queryService.fetchById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

}
