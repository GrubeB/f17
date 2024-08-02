package pl.app.comment.adapter.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.app.comment.application.domain.Voting;
import pl.app.comment.query.VotingQueryService;
import pl.app.comment.query.dto.VotingDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping(VotingQueryRestController.resourcePath)
@RequiredArgsConstructor
class VotingQueryRestController {
    public static final String resourceName = "votings";
    public static final String resourcePath = "/api/v1/" + resourceName;

    private final VotingQueryService queryService;

    @GetMapping
    Mono<ResponseEntity<Page<VotingDto>>> fetchAllByPageable(Pageable pageable) {
        return queryService.fetchByPageable(pageable)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/{id}")
    Mono<ResponseEntity<VotingDto>> fetchById(@PathVariable ObjectId id) {
        return queryService.fetchById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/{domainObjectType}/domain-objects/{domainObjectId}")
    Mono<ResponseEntity<VotingDto>> fetchByDomainObject(@PathVariable String domainObjectType, @PathVariable String domainObjectId) {
        return queryService.fetchByDomainObject(domainObjectId, domainObjectType)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/{domainObjectType}/domain-objects")
    Mono<ResponseEntity<List<VotingDto>>> fetchByDomainObject(@PathVariable String domainObjectType, @RequestParam List<String> domainObjectIds) {
        return queryService.fetchByDomainObject(domainObjectIds, domainObjectType)
                .collectList()
                .map(ResponseEntity::ok);
    }
}
