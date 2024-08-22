package pl.app.account.adapter.in;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.app.account.application.port.in.AccountCommand;
import pl.app.account.application.port.in.AccountService;
import pl.app.account.query.AccountQueryService;
import pl.app.account.query.dto.AccountDto;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(AccountRestController.resourcePath)
@RequiredArgsConstructor
class AccountRestController {
    public static final String resourceName = "accounts";
    public static final String resourcePath = "/api/v1/" + resourceName;

    private final AccountService service;
    private final AccountQueryService queryService;


    @PostMapping
    public Mono<ResponseEntity<AccountDto>> create(@RequestBody AccountCommand.CreateAccountCommand command) {
        return service.create(command)
                .flatMap(domain -> queryService.fetchById(domain.getId()))
                .map(ResponseEntity::ok);
    }
}
