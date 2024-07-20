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
import pl.app.comment.application.domain.CommentContainer;
import pl.app.comment.query.CommentContainerQueryService;

@RestController
@RequestMapping(CommentContainerQueryRestController.resourcePath)
@RequiredArgsConstructor
class CommentContainerQueryRestController {
    public static final String resourceName = "comment-containers";
    public static final String resourcePath = "/api/v1/" + resourceName;

    private final CommentContainerQueryService queryService;

    @GetMapping
    ResponseEntity<Page<CommentContainer>> fetchAllByPageable(Pageable pageable) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(queryService.fetchByPageable(pageable));
    }

    @GetMapping("/{id}")
    ResponseEntity<CommentContainer> fetchById(@PathVariable ObjectId id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(queryService.fetchById(id));
    }
    @GetMapping("/{domainObjectType}/domain-objects/{domainObjectId}")
    ResponseEntity<CommentContainer> fetchByDomainObject(@PathVariable String domainObjectType, @PathVariable String domainObjectId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(queryService.fetchByDomainObject(domainObjectId, domainObjectType));
    }
}
