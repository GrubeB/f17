package pl.app.comment.query;

import com.mongodb.lang.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.app.comment.application.domain.Comment;
import pl.app.comment.application.domain.CommentContainer;
import reactor.core.publisher.Mono;

import java.util.List;

public interface CommentContainerQueryService {
    Mono<CommentContainer> fetchById(@NonNull ObjectId id);
    Mono<Page<CommentContainer>> fetchByPageable(Pageable pageable);
    Mono<CommentContainer> fetchByDomainObject(String domainObjectId, String domainObjectType);
}
