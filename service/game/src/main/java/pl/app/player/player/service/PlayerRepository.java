package pl.app.player.player.service;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import pl.app.player.player.model.Player;

interface PlayerRepository extends ReactiveMongoRepository<Player, ObjectId> {
}
