package pl.app.unit.recruiter.adapter.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.app.unit.recruiter.application.domain.RecruiterException;
import pl.app.unit.recruiter.query.RecruiterDtoQueryService;
import pl.app.unit.recruiter.query.dto.RecruiterDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(RecruiterQueryRestController.resourcePath)
@RequiredArgsConstructor
class RecruiterQueryRestController {
    public static final String resourceName = "recruiters";
    public static final String resourcePath = "/api/v1/" + resourceName;

    private final RecruiterDtoQueryService queryService;

    @GetMapping("/{villageId}")
    Mono<ResponseEntity<RecruiterDto>> fetchByVillageId(@PathVariable ObjectId villageId) {
        return queryService.fetchByVillageId(villageId)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.error(RecruiterException.NotFoundRecruiterException.fromId(villageId.toHexString())));
    }

    @GetMapping
    Mono<ResponseEntity<Flux<RecruiterDto>>> fetchAll() {
        return Mono.just(ResponseEntity.ok(queryService.fetchAll()));
    }
}
