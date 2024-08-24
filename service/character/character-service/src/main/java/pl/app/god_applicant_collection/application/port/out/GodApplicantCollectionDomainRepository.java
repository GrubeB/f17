package pl.app.god_applicant_collection.application.port.out;

import org.bson.types.ObjectId;
import pl.app.god_applicant_collection.application.domain.GodApplicantCollection;
import reactor.core.publisher.Mono;

public interface GodApplicantCollectionDomainRepository {
    Mono<GodApplicantCollection> fetchByGodId(ObjectId godId);
}
