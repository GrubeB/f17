package pl.app.comment.application.port.out;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import pl.app.comment.application.domain.CommentContainer;

@Repository
public interface CommentContainerRepository extends MongoRepository<CommentContainer, ObjectId> {
}
