package pl.app.comment.application.port.in;

import jakarta.validation.Valid;
import org.bson.types.ObjectId;
import reactor.core.publisher.Mono;

public interface CommentRequestedService {
    Mono<ObjectId> createCommentContainerRequest(@Valid CommentCommand.CreateCommentContainerRequestCommand command);

    Mono<ObjectId> addCommentRequest(@Valid CommentCommand.AddCommentRequestCommand command);

    Mono<Void> updateCommentRequest(@Valid CommentCommand.UpdateCommentRequestCommand command);

    Mono<Void> deleteCommentRequest(@Valid CommentCommand.DeleteCommentRequestCommand command);
}
