package pl.app.comment.adapter.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.app.comment.application.domain.CommentContainer;
import pl.app.comment.query.CommentContainerQueryService;
import pl.app.comment.query.dto.CommentContainerDto;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(CommentContainerQueryRestController.resourcePath)
@RequiredArgsConstructor
class CommentContainerQueryRestController {
    public static final String resourceName = "comment-containers";
    public static final String resourcePath = "/api/v1/" + resourceName;

    private final CommentContainerQueryService queryService;

    @GetMapping
    Mono<ResponseEntity<Page<CommentContainerDto>>> fetchAllByPageable(Pageable pageable) {
        return queryService.fetchByPageable(pageable)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/{id}")
    Mono<ResponseEntity<CommentContainerDto>> fetchById(@PathVariable ObjectId id) {
        return queryService.fetchById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/{domainObjectType}/domain-objects/{domainObjectId}")
    Mono<ResponseEntity<CommentContainerDto>> fetchByDomainObject(@PathVariable String domainObjectType, @PathVariable String domainObjectId) {
        return queryService.fetchByDomainObject(domainObjectId, domainObjectType)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
