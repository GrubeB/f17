package pl.app.tower.application;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import pl.app.config.KafkaTopicConfigurationProperties;
import pl.app.monster.application.port.in.MonsterDomainRepository;
import pl.app.tower.application.domain.TowerEvent;
import pl.app.tower.application.domain.TowerException;
import pl.app.tower.application.domain.TowerLevel;
import pl.app.tower.application.port.in.TowerLevelDomainRepository;
import pl.app.tower.application.port.in.TowerService;
import pl.app.tower.in.TowerCommand;
import reactor.core.publisher.Mono;


@Service
@RequiredArgsConstructor
class TowerServiceImpl implements TowerService {
    private static final Logger logger = LoggerFactory.getLogger(TowerServiceImpl.class);

    private final ReactiveMongoTemplate mongoTemplate;
    private final KafkaTemplate<ObjectId, Object> kafkaTemplate;
    private final KafkaTopicConfigurationProperties topicNames;
    private final MonsterDomainRepository monsterDomainRepository;
    private final TowerLevelDomainRepository towerLevelDomainRepository;

    @Override
    public Mono<TowerLevel> create(TowerCommand.CreateTowerLevelCommand command) {
        logger.debug("creating tower level");
        return mongoTemplate.exists(Query.query(Criteria.where("level").is(command.getLevel())), TowerLevel.class)
                .flatMap(exist -> exist ? Mono.error(TowerException.DuplicatedLevelException.fromLevel(command.getLevel())) : Mono.empty())
                .doOnError(e -> logger.error("exception occurred while creating tower level: {}, exception: {}", command.getLevel(), e.getMessage()))
                .then(monsterDomainRepository.fetchAllById(command.getMonsterIds()))
                .flatMap(monsters -> {
                    var domain = new TowerLevel(command.getLevel(), monsters);
                    domain.setNumberOfMonsters(command.getMinNumberOfMonstersInBattle(), command.getMaxNumberOfMonstersInBattle());
                    domain.setEnergyCost(command.getEnergyCost());
                    var event = new TowerEvent.TowerLevelCreatedEvent(
                            domain.getId(),
                            domain.getLevel()
                    );
                    return mongoTemplate.insert(domain)
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getTowerLevelCreated().getName(), saved.getId(), event)).thenReturn(saved))
                            .doOnSuccess(saved -> {
                                logger.debug("created tower level {}", saved.getLevel());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            });
                });
    }

    @Override
    public Mono<TowerLevel> update(TowerCommand.UpdateTowerLevelCommand command) {
        logger.debug("updating tower level {}", command.getLevel());
        return towerLevelDomainRepository.fetchByLevel(command.getLevel())
                .doOnError(e -> logger.error("exception occurred while updating tower level {} , exception: {}", command.getLevel(), e.getMessage()))
                .zipWith(monsterDomainRepository.fetchAllById(command.getMonsterIds()))
                .flatMap(t -> {
                    var domain = t.getT1();
                    var monsters = t.getT2();
                    domain.setMonsters(monsters);
                    domain.setNumberOfMonsters(command.getMinNumberOfMonstersInBattle(), command.getMaxNumberOfMonstersInBattle());
                    domain.setEnergyCost(command.getEnergyCost());
                    var event = new TowerEvent.TowerLevelUpdatedEvent(
                            domain.getId(),
                            domain.getLevel()
                    );
                    return mongoTemplate.save(domain)
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getTowerLevelUpdated().getName(), saved.getId(), event)).thenReturn(saved))
                            .doOnSuccess(saved -> {
                                logger.debug("updated tower level {}", saved.getLevel());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            });
                });
    }

    @Override
    public Mono<TowerLevel> remove(TowerCommand.RemoveTowerLevelCommand command) {
        logger.debug("removing tower level {}", command.getLevel());
        return towerLevelDomainRepository.fetchByLevel(command.getLevel())
                .doOnError(e -> logger.error("exception occurred while removing tower level: {} , exception: {}", command.getLevel(), e.getMessage()))
                .flatMap(domain -> {
                    var event = new TowerEvent.TowerLevelRemovedEvent(
                            domain.getId(),
                            domain.getLevel()
                    );
                    return mongoTemplate.remove(domain)
                            .flatMap(unused -> Mono.fromFuture(kafkaTemplate.send(topicNames.getTowerLevelRemoved().getName(), domain.getId(), event)).thenReturn(domain))
                            .doOnSuccess(saved -> {
                                logger.debug("removed tower level {}", domain.getLevel());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            });
                });
    }
}
