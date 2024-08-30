package pl.app.god_applicant_collection.adapter.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.app.god_applicant_collection.application.port.in.GodApplicantCollectionCommand;
import pl.app.god_applicant_collection.application.port.in.GodApplicantCollectionService;
import pl.app.god_applicant_collection.query.GodApplicantCollectionQueryService;
import pl.app.god_applicant_collection.query.dto.GodApplicantCollectionDto;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(GodApplicantCollectionRestController.resourcePath)
@RequiredArgsConstructor
class GodApplicantCollectionRestController {
    public static final String resourceName = "god-applicant-collections";
    public static final String resourcePath = "/api/v1/" + resourceName;

    private final GodApplicantCollectionService service;
    private final GodApplicantCollectionQueryService queryService;


    @PostMapping("/{godId}/applicants")
    Mono<ResponseEntity<GodApplicantCollectionDto>> create(@PathVariable ObjectId godId,
                                                           @RequestBody GodApplicantCollectionCommand.CreateGodApplicantCommand command) {
        return service.create(command)
                .flatMap(domain -> queryService.fetchByGodId(domain.getGodId()))
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{godId}/applicants")
    Mono<ResponseEntity<GodApplicantCollectionDto>> remove(@PathVariable ObjectId godId,
                                                           @RequestBody GodApplicantCollectionCommand.RemoveGodApplicantCommand command) {
        return service.remove(command)
                .flatMap(domain -> queryService.fetchByGodId(domain.getGodId()))
                .map(ResponseEntity::ok);
    }

    @PostMapping("/{godId}/applicants/{characterId}/acceptances")
    Mono<ResponseEntity<GodApplicantCollectionDto>> accept(@PathVariable ObjectId godId,
                                                           @PathVariable ObjectId characterId,
                                                           @RequestBody GodApplicantCollectionCommand.AcceptGodApplicantCommand command) {
        return service.accept(command)
                .flatMap(domain -> queryService.fetchByGodId(domain.getGodId()))
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{godId}/applicants/{characterId}/acceptances")
    Mono<ResponseEntity<GodApplicantCollectionDto>> reject(@PathVariable ObjectId godId,
                                                           @PathVariable ObjectId characterId,
                                                           @RequestBody GodApplicantCollectionCommand.RejectGodApplicantCommand command) {
        return service.reject(command)
                .flatMap(domain -> queryService.fetchByGodId(domain.getGodId()))
                .map(ResponseEntity::ok);
    }
}
