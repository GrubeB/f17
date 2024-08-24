package pl.app.god_applicant_collection.application.port.in;

import pl.app.god_applicant_collection.application.domain.GodApplicantCollection;
import reactor.core.publisher.Mono;

public interface GodApplicantCollectionService {
    Mono<GodApplicantCollection> createGodApplicantCollection(GodApplicantCollectionCommand.CreateGofApplicantCollectionCommand command);
    Mono<GodApplicantCollection> create(GodApplicantCollectionCommand.CreateGodApplicantCommand command);
    Mono<GodApplicantCollection> remove(GodApplicantCollectionCommand.RemoveGodApplicantCommand command);
    Mono<GodApplicantCollection> accept(GodApplicantCollectionCommand.AcceptGodApplicantCommand command);
    Mono<GodApplicantCollection> reject(GodApplicantCollectionCommand.RejectGodApplicantCommand command);
}
