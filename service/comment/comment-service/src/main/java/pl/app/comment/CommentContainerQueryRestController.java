package pl.app.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.app.comment.command.CreateCommentContainerCommand;

@RestController
@RequestMapping(CommentContainerQueryRestController.resourcePath)
@RequiredArgsConstructor
class CommentContainerQueryRestController {
    public static final String resourceName = "comment-containers";
    public static final String resourcePath = "/api/v1/" + resourceName;

    private final CommentQueryService queryService;
}
