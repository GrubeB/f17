package pl.app.comment.query;

import com.mongodb.lang.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.app.comment.application.domain.CommentContainer;

import java.util.List;

public interface CommentContainerQueryService {
    List<CommentContainer> fetchAll();
    Page<CommentContainer> fetchByPageable(Pageable pageable);
    CommentContainer fetchById(@NonNull ObjectId id);
    CommentContainer fetchByDomainObject(String domainObjectId, String domainObjectType);
    List<CommentContainer> fetchByIds(@NonNull List<ObjectId> ids);
}
