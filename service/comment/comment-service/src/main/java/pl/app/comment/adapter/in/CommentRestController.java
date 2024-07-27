package pl.app.comment.adapter.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.app.comment.application.domain.Comment;
import pl.app.comment.application.port.in.CommentService;
import pl.app.comment.application.port.in.command.AddCommentCommand;
import pl.app.comment.application.port.in.command.AddReplyCommand;
import pl.app.comment.application.port.in.command.DeleteCommentCommand;
import pl.app.comment.application.port.in.command.UpdateCommentCommand;
import pl.app.comment.query.CommentContainerQueryService;


@RestController
@RequestMapping(CommentRestController.resourcePath)
@RequiredArgsConstructor
class CommentRestController {
    public static final String resourceName = "comments";
    public static final String resourcePath = "/api/v1/" + resourceName;

    private final CommentService service;
    private final CommentContainerQueryService queryService;

    @PostMapping
    public ResponseEntity<Comment> addComment(@RequestBody AddCommentCommand command) {
        Comment comment = service.addComment(command);
        return ResponseEntity.ok(comment);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<Void> updateComment(@PathVariable ObjectId commentId, @RequestBody UpdateCommentCommand command) {
        command.setCommentId(commentId);
        service.updateComment(command);
        return ResponseEntity
                .accepted()
                .build();
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable ObjectId commentId) {
        service.deleteComment(new DeleteCommentCommand(commentId));
        return ResponseEntity
                .accepted()
                .build();
    }

    @PostMapping("/{parentCommentId}/comments")
    public ResponseEntity<Comment> addReply(@PathVariable ObjectId parentCommentId, @RequestBody AddReplyCommand command) {
        command.setParentCommentId(parentCommentId);
        Comment comment = service.addReply(command);
        return ResponseEntity
                .ok(comment);
    }
}
