package pl.app.comment.query;

import com.mongodb.lang.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.app.comment.application.domain.CommentContainer;
import pl.app.comment.query.dto.CommentContainerDto;
import reactor.core.publisher.Mono;

public interface CommentContainerQueryService {
    Mono<CommentContainerDto> fetchById(@NonNull ObjectId id);

    Mono<Page<CommentContainerDto>> fetchByPageable(Pageable pageable);

    Mono<CommentContainerDto> fetchByDomainObject(String domainObjectId, String domainObjectType);
}
