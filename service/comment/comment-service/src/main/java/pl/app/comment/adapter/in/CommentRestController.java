package pl.app.comment.adapter.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.app.comment.application.port.in.CommentCommand;
import pl.app.comment.application.port.in.CommentRequestedService;
import pl.app.comment.query.CommentContainerQueryService;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping(CommentRestController.resourcePath)
@RequiredArgsConstructor
class CommentRestController {
    public static final String resourceName = "comments";
    public static final String resourcePath = "/api/v1/" + resourceName;

    private final CommentRequestedService commentRequestedService;
    private final CommentContainerQueryService queryService;

    @PostMapping
    Mono<ResponseEntity<ObjectId>> addComment(@RequestBody CommentCommand.AddCommentRequestCommand command) {
        return commentRequestedService.addCommentRequest(command)
                .map(ResponseEntity::ok);
    }

    @PutMapping("/{commentId}")
    Mono<ResponseEntity<Void>> updateComment(@PathVariable ObjectId commentId, @RequestBody CommentCommand.UpdateCommentRequestCommand command) {
        command.setCommentId(commentId);
        return commentRequestedService.updateCommentRequest(command)
                .then(Mono.just(ResponseEntity.accepted().build()));
    }

    @DeleteMapping("/{commentId}")
    Mono<ResponseEntity<Void>> deleteComment(@PathVariable ObjectId commentId) {
        return commentRequestedService.deleteCommentRequest(new CommentCommand.DeleteCommentRequestCommand(commentId))
                .then(Mono.just(ResponseEntity.accepted().build()));
    }

    @PostMapping("/{parentCommentId}/comments")
    Mono<ResponseEntity<ObjectId>> addReply(@PathVariable ObjectId parentCommentId, @RequestBody CommentCommand.AddCommentRequestCommand command) {
        command.setParentCommentId(parentCommentId);
        return commentRequestedService.addCommentRequest(command)
                .map(ResponseEntity::ok);
    }
}
