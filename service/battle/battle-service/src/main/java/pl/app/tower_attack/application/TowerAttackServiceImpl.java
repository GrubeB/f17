package pl.app.tower_attack.application;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import pl.app.config.KafkaTopicConfigurationProperties;
import pl.app.energy.application.port.in.EnergyCommand;
import pl.app.god.http.EnergyControllerHttpInterface;
import pl.app.god.http.GodQueryControllerHttpInterface;
import pl.app.god.query.dto.GodAggregateDto;
import pl.app.tower.dto.TowerLevelDto;
import pl.app.tower_attack.application.domain.TowerAttack;
import pl.app.tower_attack.application.domain.TowerAttackEvent;
import pl.app.tower_attack.application.domain.TowerAttackException;
import pl.app.tower_attack.application.port.in.TowerAttackCommand;
import pl.app.tower_attack.application.port.in.TowerAttackService;
import pl.app.tower_attack.application.port.out.TowerAttackDomainRepository;
import pl.app.tower_attack.application.port.out.TowerRepository;
import pl.app.unit.application.domain.BattleCharacter;
import pl.app.unit.application.port.in.CharacterRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;


@Service
@RequiredArgsConstructor
class TowerAttackServiceImpl implements TowerAttackService {
    private static final Logger logger = LoggerFactory.getLogger(TowerAttackServiceImpl.class);

    private final ReactiveMongoTemplate mongoTemplate;
    private final KafkaTemplate<ObjectId, Object> kafkaTemplate;
    private final KafkaTopicConfigurationProperties topicNames;
    private final CharacterRepository characterRepository;
    private final TowerRepository towerRepository;
    private final TowerAttackDomainRepository towerAttackDomainRepository;
    private final GodQueryControllerHttpInterface godQueryControllerHttpInterface;
    private final EnergyControllerHttpInterface energyControllerHttpInterface;

    @Override
    public Mono<TowerAttack> attackTower(TowerAttackCommand.AttackTowerCommand command) {
        logger.debug("starting attacking tower, god1({})", command.getGodId());
        return Mono.zip(
                        characterRepository.getBattleCharacterByGodId(command.getGodId(), command.getCharacterIds()),
                        towerRepository.getTowerLevel(command.getLevel()),
                        godQueryControllerHttpInterface.fetchById(command.getGodId()).map(ResponseEntity::getBody)
                )
                .doOnError(e -> logger.error("exception occurred while attacking tower, god1({}), exception: {}", command.getGodId(), e.getMessage()))
                .flatMap(t -> verifyIfThereIsEnoughEnergy(t.getT3(), t.getT2()).thenReturn(t))
                .flatMap(t -> {
                    var characters = t.getT1();
                    var towerLevel = t.getT2();
                    var god = t.getT3();
                    TowerAttack domain = new TowerAttack(command.getGodId(), characters, towerLevel, command.getNumberOfSeconds());
                    var event = new TowerAttackEvent.TowerAttackStartedEvent(
                            domain.getInfo().getTowerAttackId(),
                            command.getCharacterIds().stream().toList()
                    );
                    return Mono.when(
                            Mono.just(towerAttackDomainRepository.save(domain)),
                            mongoTemplate.insert(domain.getFinishManager().getResult()),
                            Mono.fromFuture(kafkaTemplate.send(topicNames.getTowerAttackStarted().getName(), domain.getInfo().getTowerAttackId(), event))
                    ).doOnSuccess(unused -> {
                        logger.debug("started tower attack {}", domain.getInfo().getTowerAttackId());
                        logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                    }).then(Mono.fromRunnable(() -> {
                        handleAttackTower(domain.getInfo().getTowerAttackId()).subscribeOn(Schedulers.boundedElastic())
                                .doOnSuccess(id -> {
                                    var towerAttack = towerAttackDomainRepository.getTowerAttack(id);
                                    var result = towerAttack.getFinishManager().getResult();
                                    var towerAttackEndedEvent = new TowerAttackEvent.TowerAttackEndedEvent(towerAttack.getInfo().getTowerAttackId(), result.getCharacterResults());
                                    Mono.when(
                                                    mongoTemplate.save(result),
                                                    mongoTemplate.insert(result.getLog()),
                                                    Mono.fromFuture(kafkaTemplate.send(topicNames.getTowerAttackEnded().getName(), result.getTowerAttackId(), towerAttackEndedEvent)),
                                                    Flux.fromIterable(result.getBattleResults())
                                                            .flatMap(e -> mongoTemplate.insert(e))
                                                            .flatMap(e -> mongoTemplate.insert(e.getLog())).then())
                                            .doOnSuccess(unused -> {
                                                logger.debug("send {} - {}", towerAttackEndedEvent.getClass().getSimpleName(), towerAttackEndedEvent);
                                                logger.debug("ended tower attack {}", domain.getInfo().getTowerAttackId());
                                            })
                                            .subscribe();
                                })
                                .subscribe();
                    })).thenReturn(domain);
                });
    }

    private Mono<?> verifyIfThereIsEnoughEnergy(GodAggregateDto god, TowerLevelDto towerLevel) {
        var energyCost = towerLevel.getEnergyCost();
        var godEnergy = god.getEnergy().getAmount();
        if (godEnergy < energyCost) {
            throw new TowerAttackException.NotEnoughEnergyException();
        }
        return energyControllerHttpInterface.subtractEnergy(god.getId(), new EnergyCommand.SubtractEnergyCommand(god.getId(), (long) energyCost));
    }

    private Mono<ObjectId> handleAttackTower(ObjectId towerAttackId) {
        return Mono.just(towerAttackId)
                .flatMap(this::handleFirstPhase)
                .flatMap(this::handleSecondPhase)
                .flatMap(this::repeatIfNeeded);
    }

    private Mono<ObjectId> handleFirstPhase(ObjectId id) {
        var domain = towerAttackDomainRepository.getTowerAttack(id);
        if (isTowerAttackEnded(domain)) {
            return Mono.just(id);
        }
        Instant phraseEnd = domain.getBattleManager().teamWalk();
        towerAttackDomainRepository.save(domain);
        return Mono.when(
                mongoTemplate.save(domain.getFinishManager().getResult())
        ).then(waitUntil(phraseEnd)).thenReturn(id);

    }

    private Mono<ObjectId> handleSecondPhase(ObjectId id) {
        var domain = towerAttackDomainRepository.getTowerAttack(id);

        if (isTowerAttackEnded(domain)) {
            return Mono.just(id);
        }
        Instant phraseEnd = domain.getBattleManager().teamStartNewBattle();
        towerAttackDomainRepository.save(domain);
        return Mono.when(
                mongoTemplate.save(domain.getFinishManager().getResult())
        ).then(waitUntil(phraseEnd)).thenReturn(id);

    }

    private Mono<ObjectId> repeatIfNeeded(ObjectId id) {
        var domain = towerAttackDomainRepository.getTowerAttack(id);

        if (isTowerAttackEnded(domain)) {
            return Mono.just(id);
        } else {
            return handleAttackTower(id);
        }
    }

    private static Mono<Void> waitUntil(Instant targetTime) {
        Instant now = Instant.now();
        long delayInMillis = ChronoUnit.MILLIS.between(now, targetTime);
        return Mono.delay(Duration.ofMillis(delayInMillis)).then();
    }

    private boolean isTowerAttackEnded(TowerAttack domain) {
        return domain.getFinishManager().getResult().getTowerAttackEnded();
    }
}
