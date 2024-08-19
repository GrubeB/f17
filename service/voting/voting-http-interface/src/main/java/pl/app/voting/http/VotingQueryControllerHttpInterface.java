package pl.app.voting.http;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import pl.app.voting.query.dto.VotingDto;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;

@HttpExchange(
        url = "votings",
        accept = MediaType.APPLICATION_JSON_VALUE,
        contentType = MediaType.APPLICATION_JSON_VALUE
)
public interface VotingQueryControllerHttpInterface {
    @GetExchange
    Mono<ResponseEntity<Page<VotingDto>>> fetchAllByPageable(Pageable pageable);

    @GetExchange("/{id}")
    Mono<ResponseEntity<VotingDto>> fetchById(@PathVariable ObjectId id);

    @GetExchange("/{domainObjectType}/domain-objects/{domainObjectId}")
    Mono<ResponseEntity<VotingDto>> fetchByDomainObject(@PathVariable String domainObjectType, @PathVariable String domainObjectId);

    @GetExchange("/{domainObjectType}/domain-objects")
    Mono<ResponseEntity<List<VotingDto>>> fetchByDomainObject(@PathVariable String domainObjectType, @RequestParam Collection<String> domainObjectIds);
}
