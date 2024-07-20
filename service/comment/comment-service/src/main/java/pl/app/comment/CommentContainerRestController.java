package pl.app.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.app.comment.command.CreateCommentContainerCommand;

@RestController
@RequestMapping(CommentContainerRestController.resourcePath)
@RequiredArgsConstructor
class CommentContainerRestController {
        public static final String resourceName = "comment-containers";
    public static final String resourcePath = "/api/v1/" + resourceName;

    private final CommentService service;
    private final CommentQueryService queryService;

    @PostMapping
    public ResponseEntity<CommentContainer> create(@RequestBody CreateCommentContainerCommand command) {
        CommentContainer commentContainer = service.createCommentContainer(command);
        return ResponseEntity.ok(commentContainer);
    }
}
