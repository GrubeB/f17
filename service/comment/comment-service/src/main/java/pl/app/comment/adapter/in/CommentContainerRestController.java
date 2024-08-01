package pl.app.comment.adapter.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.app.comment.application.port.in.CommentCommand;
import pl.app.comment.application.port.in.CommentRequestedService;

@RestController
@RequestMapping(CommentContainerRestController.resourcePath)
@RequiredArgsConstructor
class CommentContainerRestController {
    public static final String resourceName = "comment-containers";
    public static final String resourcePath = "/api/v1/" + resourceName;

    private final CommentRequestedService commentRequestedService;

    @PostMapping
    public ResponseEntity<ObjectId> create(@RequestBody CommentCommand.CreateCommentContainerRequestCommand command) {
        ObjectId id = commentRequestedService.createCommentContainerRequest(command);
        return ResponseEntity.ok(id);
    }
}
