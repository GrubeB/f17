package pl.app.comment.application.port.in;

import jakarta.validation.Valid;
import pl.app.comment.application.domain.Comment;
import pl.app.comment.application.domain.CommentContainer;
import reactor.core.publisher.Mono;

public interface CommentService {
    Mono<CommentContainer> createCommentContainer(@Valid CommentCommand.CreateCommentContainerCommand command);

    Mono<CommentContainer> addComment(@Valid CommentCommand.AddCommentCommand command);

    Mono<CommentContainer> updateComment(@Valid CommentCommand.UpdateCommentCommand command);

    Mono<CommentContainer> deleteComment(@Valid CommentCommand.DeleteCommentCommand command);
}
