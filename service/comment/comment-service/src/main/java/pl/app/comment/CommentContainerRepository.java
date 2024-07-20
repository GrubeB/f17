package pl.app.comment;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentContainerRepository extends MongoRepository<CommentContainer, ObjectId> {
}
