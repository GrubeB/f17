package pl.app.comment;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.app.comment.command.*;


@RestController
@RequestMapping(CommentRestController.resourcePath)
@RequiredArgsConstructor
class CommentRestController {
    public static final String resourceName = "comments";
    public static final String resourcePath = "/api/v1/" + resourceName;

    private final CommentService service;
    private final CommentQueryService queryService;
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
    public ResponseEntity<Comment> addReply(@PathVariable ObjectId parentCommentId,  @RequestBody AddReplyCommand command) {
        command.setParentCommentId(parentCommentId);
        Comment comment = service.addReply(command);
        return ResponseEntity
                .ok(comment);
    }
}
