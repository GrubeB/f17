package pl.app.battle.application;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import pl.app.battle.application.domain.*;
import pl.app.battle.application.port.in.BattleCommand;
import pl.app.battle.application.port.in.BattleService;
import pl.app.battle.application.port.out.CharacterRepository;
import pl.app.battle.application.port.out.MonsterRepository;
import pl.app.config.KafkaTopicConfigurationProperties;
import reactor.core.publisher.Flux;
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
    private final MonsterRepository monsterRepository;

    @Override
    public Mono<BattleResult> startDuelBattle(BattleCommand.StartDuelBattleCommand command) {
        logger.debug("starting battle, team1({}) vs team2({})", command.getCharacterId1(), command.getCharacterId2());
        return Mono.zip(characterRepository.getBattleCharacterById(command.getCharacterId1()),
                        characterRepository.getBattleCharacterById(command.getCharacterId2())
                ).doOnError(e -> logger.error("exception occurred while starting battle, exception: {}", e.getMessage()))
                .flatMap(characters -> {
                    Battle battle = new Battle(Set.of(characters.getT1()), Set.of(characters.getT2()));
                    battle.startBattle();
                    BattleResult battleResult = battle.getBattleResult();
                    var event = new BattleEvent.BattleEndedEvent(battleResult.getBattleId());
                    return mongoTemplate.insert(battleResult.getLog())
                            .flatMap(saved -> mongoTemplate.insert(battleResult))
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getBattleEnded().getName(), saved.getBattleId(), event)).thenReturn(saved))
                            .doOnSuccess(saved -> {
                                logger.debug("ended battle:{}, team1({}) vs team2({})", saved.getBattleId(), command.getCharacterId1(), command.getCharacterId2());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            });
                });
    }

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
                    BattleResult result = battle.getBattleResult();
                    return saveBattleResultAndPublishEvent(result)
                            .doOnSuccess(saved -> {
                                logger.debug("ended battle {}, god1({}) vs god2({})", saved.getBattleId(), command.getGod1(), command.getGod2());
                            });
                });
    }

    private Mono<BattleResult> saveBattleResultAndPublishEvent(BattleResult result) {
        var event = new BattleEvent.BattleEndedEvent(result.getBattleId());
        return Mono.when(
                        mongoTemplate.insert(result),
                        mongoTemplate.insert(result.getLog()),
                        Mono.fromFuture(kafkaTemplate.send(topicNames.getBattleEnded().getName(), result.getBattleId(), event))
                ).doOnSuccess(unused -> logger.debug("send {} - {}", event.getClass().getSimpleName(), event))
                .thenReturn(result);
    }


    @Override
    public Mono<TowerAttackResult> attackTower(BattleCommand.AttackTowerCommand command) {
        logger.debug("starting attacking tower, god1({})", command.getGodId());
        return Mono.zip(characterRepository.getBattleCharacterByGodId(command.getGodId(), command.getCharacterIds()),
                        monsterRepository.getByTowerLevel(command.getLevel()))
                .doOnError(e -> logger.error("exception occurred while attacking tower, god1({}), exception: {}", command.getGodId(), e.getMessage()))
                .flatMap(t -> {
                    Set<BattleCharacter> characters = t.getT1();
                    Set<BattleCharacter> monsters = t.getT2();
                    TowerAttack domain = new TowerAttack(command.getGodId(), characters, monsters);
                    domain.start();
                    return saveTowerAttackResult(domain.getResult())
                            .doOnSuccess(saved -> {
                                logger.debug("ended tower attack {}", saved.getTowerAttackId());
                            });
                });
    }

    private Mono<TowerAttackResult> saveTowerAttackResult(TowerAttackResult result) {
        var event = new TowerAttackEvent.TowerAttackEndedEvent(result.getTowerAttackId());
        return Mono.when(
                        mongoTemplate.insert(result),
                        mongoTemplate.insert(result.getLog()),
                        Mono.fromFuture(kafkaTemplate.send(topicNames.getTowerAttackEnded().getName(), result.getTowerAttackId(), event)),
                        Flux.fromIterable(result.getBattleResults()).flatMap(this::saveBattleResultAndPublishEvent).then()
                ).doOnSuccess(unused -> logger.debug("send {} - {}", event.getClass().getSimpleName(), event))
                .thenReturn(result);
    }
}
