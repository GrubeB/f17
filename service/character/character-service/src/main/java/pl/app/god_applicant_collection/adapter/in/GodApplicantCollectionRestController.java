package pl.app.god_applicant_collection.adapter.in;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.app.god_applicant_collection.application.port.in.GodApplicantCollectionCommand;
import pl.app.god_applicant_collection.application.port.in.GodApplicantCollectionService;
import pl.app.god_applicant_collection.query.GodApplicantCollectionQueryService;
import pl.app.god_applicant_collection.query.dto.GodApplicantCollectionDto;
import pl.app.god_family.query.dto.GodFamilyDto;
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
    public Mono<ResponseEntity<GodApplicantCollectionDto>> create(@RequestBody GodApplicantCollectionCommand.CreateGodApplicantCommand command) {
        return service.create(command)
                .flatMap(domain -> queryService.fetchByGodId(domain.getGodId()))
                .map(ResponseEntity::ok);
    }
    @DeleteMapping("/{godId}/applicants")
    public Mono<ResponseEntity<GodApplicantCollectionDto>> remove(@RequestBody GodApplicantCollectionCommand.RemoveGodApplicantCommand command) {
        return service.remove(command)
                .flatMap(domain -> queryService.fetchByGodId(domain.getGodId()))
                .map(ResponseEntity::ok);
    }
    @PostMapping("/{godId}/applicants/{characterId}/acceptances")
    public Mono<ResponseEntity<GodApplicantCollectionDto>> accept(@RequestBody GodApplicantCollectionCommand.AcceptGodApplicantCommand command) {
        return service.accept(command)
                .flatMap(domain -> queryService.fetchByGodId(domain.getGodId()))
                .map(ResponseEntity::ok);
    }
    @DeleteMapping("/{godId}/applicants/{characterId}/acceptances")
    public Mono<ResponseEntity<GodApplicantCollectionDto>> reject(@RequestBody GodApplicantCollectionCommand.RejectGodApplicantCommand command) {
        return service.reject(command)
                .flatMap(domain -> queryService.fetchByGodId(domain.getGodId()))
                .map(ResponseEntity::ok);
    }
}
