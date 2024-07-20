package pl.app.comment.adapter.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.app.comment.application.domain.Comment;
import pl.app.comment.query.CommentQueryService;

@RestController
@RequestMapping(CommentQueryRestController.resourcePath)
@RequiredArgsConstructor
class CommentQueryRestController {
    public static final String resourceName = "comments";
    public static final String resourcePath = "/api/v1/" + resourceName;

    private final CommentQueryService queryService;

    @GetMapping
    ResponseEntity<Page<Comment>> fetchAllByPageable(Pageable pageable) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(queryService.fetchByPageable(pageable));
    }

    @GetMapping("/{id}")
    ResponseEntity<Comment> fetchById(@PathVariable ObjectId id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(queryService.fetchById(id));
    }
}
