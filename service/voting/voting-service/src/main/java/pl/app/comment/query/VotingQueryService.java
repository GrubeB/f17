package pl.app.comment.query;

import com.mongodb.lang.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.app.comment.application.domain.Voting;

import java.util.List;

public interface VotingQueryService {
    List<Voting> fetchAll();

    Page<Voting> fetchByPageable(Pageable pageable);

    Voting fetchById(@NonNull ObjectId id);

    Voting fetchByDomainObject(String domainObjectId, String domainObjectType);

    List<Voting> fetchByIds(@NonNull List<ObjectId> ids);
}
