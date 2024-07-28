package pl.app.comment.application.port.in;

import jakarta.validation.Valid;
import pl.app.comment.application.domain.Comment;
import pl.app.comment.application.domain.CommentContainer;
import pl.app.comment.application.port.in.command.*;

public interface CommentService {
    CommentContainer createCommentContainer(@Valid CreateCommentContainerCommand command);

    Comment addComment(@Valid AddCommentCommand command);

    Comment addReply(@Valid AddReplyCommand command);

    void updateComment(@Valid UpdateCommentCommand command);

    void deleteComment(@Valid DeleteCommentCommand command);
}
