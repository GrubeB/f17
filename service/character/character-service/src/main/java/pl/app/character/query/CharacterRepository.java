package pl.app.character.query;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import pl.app.character.application.domain.Character;
import reactor.core.publisher.Flux;

@Repository
interface CharacterRepository extends ReactiveMongoRepository<Character, ObjectId> {
    Flux<Character> findAllBy(Pageable pageable);
}