package pl.app.comment.query;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import pl.app.comment.application.domain.CommentContainer;
import reactor.core.publisher.Flux;

@Repository
interface CommentContainerRepository extends ReactiveMongoRepository<CommentContainer, ObjectId> {
    Flux<CommentContainer> findAllBy(Pageable pageable);
}
