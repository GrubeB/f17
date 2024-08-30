package pl.app.recruitment.application.port.out;

import org.bson.types.ObjectId;
import pl.app.god.application.domain.God;
import pl.app.god_applicant_collection.application.domain.GodApplicantCollection;
import pl.app.god_applicant_collection.query.dto.GodApplicantCollectionDto;
import reactor.core.publisher.Mono;

public interface GodApplicantCreator {
    Mono<Void> create(ObjectId godId, ObjectId characterId);
}
