package pl.app.comment.query;

import com.mongodb.lang.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.app.comment.application.domain.Comment;

import java.util.List;

public interface CommentQueryService {
    List<Comment> fetchAll();

    Page<Comment> fetchByPageable(Pageable pageable);

    Comment fetchById(@NonNull ObjectId id);

    List<Comment> fetchByIds(@NonNull List<ObjectId> ids);
}
