package pl.app.god_applicant_collection.query;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import pl.app.god_applicant_collection.query.dto.GodApplicantCollectionDto;
import reactor.core.publisher.Mono;

public interface GodApplicantCollectionQueryService {
    Mono<GodApplicantCollectionDto> fetchByGodId(@NonNull ObjectId id);

    Mono<Page<GodApplicantCollectionDto>> fetchAllByPageable(Pageable pageable);
}
