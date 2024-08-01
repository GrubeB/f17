package pl.app.comment.query;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import pl.app.comment.application.domain.Voting;

@Repository
interface VotingRepository extends MongoRepository<Voting, ObjectId> {
}
