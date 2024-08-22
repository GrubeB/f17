package pl.app.voting.query;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import pl.app.voting.application.domain.Voting;
import reactor.core.publisher.Flux;

@Repository
interface VotingRepository extends ReactiveMongoRepository<Voting, ObjectId> {
    Flux<Voting> findAllBy(Pageable pageable);
}