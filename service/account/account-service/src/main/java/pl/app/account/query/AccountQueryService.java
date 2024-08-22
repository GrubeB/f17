package pl.app.account.query;

import com.mongodb.lang.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.app.account.query.dto.AccountDto;
import reactor.core.publisher.Mono;

public interface AccountQueryService {
    Mono<AccountDto> fetchById(@NonNull ObjectId id);

    Mono<Page<AccountDto>> fetchByPageable(Pageable pageable);
}
