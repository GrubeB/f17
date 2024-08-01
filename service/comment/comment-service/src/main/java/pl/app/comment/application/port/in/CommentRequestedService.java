package pl.app.comment.application.port.in;

import jakarta.validation.Valid;
import org.bson.types.ObjectId;

public interface CommentRequestedService {
    ObjectId createCommentContainerRequest(@Valid CommentCommand.CreateCommentContainerRequestCommand command);

    ObjectId addCommentRequest(@Valid CommentCommand.AddCommentRequestCommand command);

    void updateCommentRequest(@Valid CommentCommand.UpdateCommentRequestCommand command);

    void deleteCommentRequest(@Valid CommentCommand.DeleteCommentRequestCommand command);
}
