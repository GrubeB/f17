package pl.app.comment;

import jakarta.validation.Valid;
import org.bson.types.ObjectId;
import pl.app.comment.command.*;

public interface CommentService {
    CommentContainer createCommentContainer(@Valid CreateCommentContainerCommand command);
    Comment addComment(@Valid AddCommentCommand command);
    Comment addReply(@Valid AddReplyCommand command);
    void updateComment(@Valid UpdateCommentCommand command);
    void deleteComment(@Valid DeleteCommentCommand command);
}
