package pl.app.comment.query;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import pl.app.comment.application.domain.Comment;
import reactor.core.publisher.Flux;

@Repository
interface CommentRepository extends ReactiveMongoRepository<Comment, ObjectId> {
    Flux<Comment> findAllBy(Pageable pageable);
}
