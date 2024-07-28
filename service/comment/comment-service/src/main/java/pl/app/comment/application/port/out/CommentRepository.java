package pl.app.comment.application.port.out;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import pl.app.comment.application.domain.Comment;

@Repository
public interface CommentRepository extends MongoRepository<Comment, ObjectId> {
}
