package pl.app.comment.adapter.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.app.comment.application.port.in.CommentCommand;
import pl.app.comment.application.port.in.CommentRequestedService;
import pl.app.comment.query.CommentContainerQueryService;


@RestController
@RequestMapping(CommentRestController.resourcePath)
@RequiredArgsConstructor
class CommentRestController {
    public static final String resourceName = "comments";
    public static final String resourcePath = "/api/v1/" + resourceName;

    private final CommentRequestedService commentRequestedService;
    private final CommentContainerQueryService queryService;

    @PostMapping
    public ResponseEntity<ObjectId> addComment(@RequestBody CommentCommand.AddCommentRequestCommand command) {
        ObjectId id = commentRequestedService.addCommentRequest(command);
        return ResponseEntity
                .accepted()
                .body(id);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<Void> updateComment(@PathVariable ObjectId commentId, @RequestBody CommentCommand.UpdateCommentRequestCommand command) {
        command.setCommentId(commentId);
        commentRequestedService.updateCommentRequest(command);
        return ResponseEntity
                .accepted()
                .build();
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable ObjectId commentId) {
        commentRequestedService.deleteCommentRequest(new CommentCommand.DeleteCommentRequestCommand(commentId));
        return ResponseEntity
                .accepted()
                .build();
    }

    @PostMapping("/{parentCommentId}/comments")
    public ResponseEntity<ObjectId> addReply(@PathVariable ObjectId parentCommentId, @RequestBody CommentCommand.AddCommentRequestCommand command) {
        command.setParentCommentId(parentCommentId);
        ObjectId id = commentRequestedService.addCommentRequest(command);
        return ResponseEntity
                .accepted()
                .body(id);
    }
}
