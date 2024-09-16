package pl.app.battle.application;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import pl.app.battle.application.domain.Battle;
import pl.app.unit.application.domain.BattleCharacter;
import pl.app.battle.application.domain.BattleEvent;
import pl.app.battle.application.domain.BattleResult;
import pl.app.battle.application.port.in.BattleCommand;
import pl.app.battle.application.port.in.BattleService;
import pl.app.config.KafkaTopicConfigurationProperties;
import pl.app.unit.application.port.in.CharacterRepository;
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
    public Mono<BattleResult> startTwoGodBattle(BattleCommand.StartTwoGodBattleCommand command) {
        logger.debug("starting battle, god1({}) vs god2({})", command.getGod1(), command.getGod2());
        return Mono.zip(characterRepository.getBattleCharacterByGodId(command.getGod1(), command.getGod1CharacterIds()),
                        characterRepository.getBattleCharacterByGodId(command.getGod2(), command.getGod2CharacterIds())
                ).doOnError(e -> logger.error("exception occurred while starting battle, god1({}) vs god2({}), exception: {}", command.getGod1(), command.getGod2(), e.getMessage()))
                .flatMap(teams -> {
                    Set<BattleCharacter> team1 = teams.getT1();
                    Set<BattleCharacter> team2 = teams.getT2();
                    Battle battle = new Battle(team1, team2);
                    battle.startBattle();
                    BattleResult result = battle.getFinishManager().getBattleResult();
                    var event = new BattleEvent.BattleEndedEvent(result.getBattleId(), result.getCharacterResults());
                    return Mono.when(
                                    mongoTemplate.insert(result),
                                    mongoTemplate.insert(result.getLog()),
                                    Mono.fromFuture(kafkaTemplate.send(topicNames.getBattleEnded().getName(), result.getBattleId(), event))
                            )
                            .doOnSuccess(saved -> {
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                                logger.debug("ended battle {}, god1({}) vs god2({})", battle.getInfo().getBattleId(), command.getGod1(), command.getGod2());
                            }).thenReturn(result);
                });
    }
}
