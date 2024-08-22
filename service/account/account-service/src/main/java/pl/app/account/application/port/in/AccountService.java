package pl.app.account.application.port.in;

import pl.app.account.application.domain.Account;
import reactor.core.publisher.Mono;


public interface AccountService {
    Mono<Account> create(AccountCommand.CreateAccountCommand command);
}
