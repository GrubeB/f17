package pl.app.character.http;

import org.bson.types.ObjectId;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import pl.app.god_applicant_collection.application.port.in.GodApplicantCollectionCommand;
import pl.app.god_applicant_collection.query.dto.GodApplicantCollectionDto;
import reactor.core.publisher.Mono;

@HttpExchange(
        url = "god-applicant-collections",
        accept = MediaType.APPLICATION_JSON_VALUE,
        contentType = MediaType.APPLICATION_JSON_VALUE
)
public interface GodApplicantCollectionControllerHttpInterface {
    @PostExchange("/{godId}/applicants")
    Mono<ResponseEntity<GodApplicantCollectionDto>> create(@PathVariable ObjectId godId,
                                                           @RequestBody GodApplicantCollectionCommand.CreateGodApplicantCommand command);

    @DeleteExchange("/{godId}/applicants")
    Mono<ResponseEntity<GodApplicantCollectionDto>> remove(@PathVariable ObjectId godId,
                                                           @RequestBody GodApplicantCollectionCommand.RemoveGodApplicantCommand command);

    @PostExchange("/{godId}/applicants/{characterId}/acceptances")
    Mono<ResponseEntity<GodApplicantCollectionDto>> accept(@PathVariable ObjectId godId,
                                                           @PathVariable ObjectId characterId,
                                                           @RequestBody GodApplicantCollectionCommand.AcceptGodApplicantCommand command);

    @DeleteExchange("/{godId}/applicants/{characterId}/acceptances")
    Mono<ResponseEntity<GodApplicantCollectionDto>> reject(@PathVariable ObjectId godId,
                                                           @PathVariable ObjectId characterId,
                                                           @RequestBody GodApplicantCollectionCommand.RejectGodApplicantCommand command);

}
