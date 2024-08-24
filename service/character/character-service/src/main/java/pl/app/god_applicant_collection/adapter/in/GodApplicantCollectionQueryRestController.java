package pl.app.god_applicant_collection.adapter.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.app.god_applicant_collection.application.domain.GodApplicantCollection;
import pl.app.god_applicant_collection.application.domain.GodApplicantCollectionException;
import pl.app.god_applicant_collection.query.GodApplicantCollectionQueryService;
import pl.app.god_applicant_collection.query.dto.GodApplicantCollectionDto;
import pl.app.god_family.application.domain.GodFamilyException;
import pl.app.god_family.query.dto.GodFamilyDto;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping(GodApplicantCollectionQueryRestController.resourcePath)
@RequiredArgsConstructor
class GodApplicantCollectionQueryRestController {
    public static final String resourceName = "god-applicant-collections";
    public static final String resourcePath = "/api/v1/" + resourceName;

    private final GodApplicantCollectionQueryService queryService;

    @GetMapping("/{godId}")
    Mono<ResponseEntity<GodApplicantCollectionDto>> fetchByGodId(@PathVariable ObjectId godId) {
        return queryService.fetchByGodId(godId)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.error(GodApplicantCollectionException.NotFoundGodApplicantCollectionException.fromGodId(godId.toHexString())));
    }
    @GetMapping
    Mono<ResponseEntity<Page<GodApplicantCollectionDto>>> fetchAllByPageable(Pageable pageable) {
        return queryService.fetchAllByPageable(pageable)
                .map(ResponseEntity::ok);
    }
}
