package pl.app.comment.query;

import com.mongodb.lang.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.app.voting.query.dto.VotingDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface VotingQueryService {
    Mono<VotingDto> fetchById(@NonNull ObjectId id);

    Mono<Page<VotingDto>> fetchByPageable(Pageable pageable);

    Mono<VotingDto> fetchByDomainObject(String domainObjectId, String domainObjectType);

    Flux<VotingDto> fetchByDomainObject(List<String> domainObjectId, String domainObjectType);
}
