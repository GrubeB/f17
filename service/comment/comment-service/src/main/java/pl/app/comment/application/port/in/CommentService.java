package pl.app.comment.application.port.in;

import jakarta.validation.Valid;
import pl.app.comment.application.domain.Comment;
import pl.app.comment.application.domain.CommentContainer;
import pl.app.comment.application.port.in.CommentCommand.*;

public interface CommentService {
    CommentContainer createCommentContainer(@Valid CommentCommand.CreateCommentContainerCommand command);

    Comment addComment(@Valid CommentCommand.AddCommentCommand command);

    void updateComment(@Valid CommentCommand.UpdateCommentCommand command);

    void deleteComment(@Valid CommentCommand.DeleteCommentCommand command);
}
