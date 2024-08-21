package pl.app.battle.application;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import pl.app.battle.application.domain.Battle;
import pl.app.battle.application.domain.BattleEvent;
import pl.app.battle.application.domain.BattleResult;
import pl.app.battle.application.port.in.BattleCommand;
import pl.app.battle.application.port.in.BattleService;
import pl.app.battle.application.port.out.CharacterRepository;
import pl.app.config.KafkaTopicConfigurationProperties;
import reactor.core.publisher.Mono;

import java.util.Set;


@Service
@RequiredArgsConstructor
class BattleServiceImpl implements BattleService {
    private static final Logger logger = LoggerFactory.getLogger(BattleServiceImpl.class);

    private final ReactiveMongoTemplate mongoTemplate;
    private final KafkaTemplate<ObjectId, Object> kafkaTemplate;
    private final KafkaTopicConfigurationProperties topicNames;
    private final CharacterRepository characterRepository;

    @Override
    public Mono<BattleResult> startDuelBattle(BattleCommand.StartDuelBattleCommand command) {
        logger.debug("starting battle, team1({}) vs team2({})", command.getPlayer1(), command.getPlayer2());
        return Mono.zip(characterRepository.getBattleCharacterById(command.getPlayer1()),
                        characterRepository.getBattleCharacterById(command.getPlayer2())
                ).doOnError(e -> logger.error("exception occurred while starting battle, exception: {}", e.getMessage()))
                .flatMap(players -> {
                    Battle battle = new Battle(Set.of(players.getT1()), Set.of(players.getT2()));
                    battle.startBattle();
                    BattleResult battleResult = battle.getBattleResult();
                    var event = new BattleEvent.BattleEndedEvent(battleResult.getBattleId());
                    return mongoTemplate.insert(battleResult.getLog())
                            .flatMap(saved -> mongoTemplate.insert(battleResult))
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getBattleEnded().getName(), saved.getBattleId(), event)).thenReturn(saved))
                            .doOnSuccess(saved -> {
                                logger.debug("ended battle:{}, team1({}) vs team2({})", saved.getBattleId(), command.getPlayer1(), command.getPlayer2());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            });
                });
    }
}
