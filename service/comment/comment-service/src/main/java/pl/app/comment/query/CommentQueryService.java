package pl.app.comment.query;

import com.mongodb.lang.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.app.comment.application.domain.Comment;
import reactor.core.publisher.Mono;

public interface CommentQueryService {
    Mono<Comment> fetchById(@NonNull ObjectId id);

    Mono<Page<Comment>> fetchByPageable(Pageable pageable);
}
