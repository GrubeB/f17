package pl.app.monster_gear.application;

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
import pl.app.item.application.port.in.ItemDomainRepository;
import pl.app.monster_gear.application.domain.MonsterGear;
import pl.app.monster_gear.application.domain.MonsterGearEvent;
import pl.app.monster_gear.application.domain.MonsterGearException;
import pl.app.monster_gear.application.port.in.MonsterGearCommand;
import pl.app.monster_gear.application.port.in.MonsterGearDomainRepository;
import pl.app.monster_gear.application.port.in.MonsterGearService;
import reactor.core.publisher.Mono;


@Service
@RequiredArgsConstructor
class MonsterGearServiceImpl implements MonsterGearService {
    private static final Logger logger = LoggerFactory.getLogger(MonsterGearServiceImpl.class);

    private final ReactiveMongoTemplate mongoTemplate;
    private final KafkaTemplate<ObjectId, Object> kafkaTemplate;
    private final KafkaTopicConfigurationProperties topicNames;

    private final MonsterGearDomainRepository monsterGearDomainRepository;
    private final ItemDomainRepository itemDomainRepository;


    @Override
    public Mono<MonsterGear> crateMonsterGear(MonsterGearCommand.CreateMonsterGearCommand command) {
        logger.debug("creating monster gear for monster: {}", command.getMonsterId());
        return mongoTemplate.exists(Query.query(Criteria.where("monsterId").is(command.getMonsterId())), MonsterGear.class)
                .flatMap(exist -> exist ? Mono.error(MonsterGearException.DuplicatedMonsterGearException.fromId(command.getMonsterId().toHexString())) : Mono.empty())
                .doOnError(e -> logger.error("exception occurred while creating  monster gear for monster: {}, exception: {}", command.getMonsterId(), e.getMessage()))
                .then(Mono.defer(() -> {
                    var domain = new MonsterGear(command.getMonsterId());
                    var event = new MonsterGearEvent.MonsterGearCreatedEvent(
                            domain.getId(),
                            domain.getMonsterId()
                    );
                    return mongoTemplate.insert(domain)
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getMonsterGearCreated().getName(), saved.getId(), event)).thenReturn(saved))
                            .doOnSuccess(saved -> {
                                logger.debug("created monster gear {} for monster: {}", saved.getId(), saved.getMonsterId());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            });
                }));
    }

    @Override
    public Mono<MonsterGear> removeMonsterGear(MonsterGearCommand.RemoveMonsterGearCommand command) {
        logger.debug("removing monster gear for monster: {}", command.getMonsterId());
        return monsterGearDomainRepository.fetchByMonsterId(command.getMonsterId())
                .doOnError(e -> logger.error("exception occurred while removing  monster gear for monster: {}, exception: {}", command.getMonsterId(), e.getMessage()))
                .flatMap(domain -> {
                    var event = new MonsterGearEvent.MonsterGearRemovedEvent(
                            domain.getId(),
                            domain.getMonsterId()
                    );
                    return mongoTemplate.remove(domain)
                            .flatMap(unused -> Mono.fromFuture(kafkaTemplate.send(topicNames.getMonsterGearRemoved().getName(), domain.getId(), event)).thenReturn(unused))
                            .doOnSuccess(unused -> {
                                logger.debug("removed monster gear {} for monster: {}", domain.getId(), domain.getMonsterId());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            }).thenReturn(domain);
                });
    }

    @Override
    public Mono<MonsterGear> setMonsterItem(MonsterGearCommand.SetMonsterItemCommand command) {
        logger.debug("setting item to monster: {}", command.getMonsterId());
        return monsterGearDomainRepository.fetchByMonsterId(command.getMonsterId())
                .doOnError(e -> logger.error("exception occurred while setting item to monster: {}, exception: {}", command.getMonsterId(), e.getMessage()))
                .zipWith(itemDomainRepository.fetchById(command.getItemId()))
                .flatMap(t -> {
                    var domain = t.getT1();
                    var item = t.getT2();
                    domain.setItem(item, command.getSlot());
                    var event = new MonsterGearEvent.MonsterItemSetEvent(
                            domain.getId(),
                            domain.getMonsterId(),
                            item.getId(),
                            command.getSlot()
                    );
                    return mongoTemplate.save(domain)
                            .flatMap(unused -> Mono.fromFuture(kafkaTemplate.send(topicNames.getMonsterItemSet().getName(), domain.getId(), event)).thenReturn(unused))
                            .doOnSuccess(unused -> {
                                logger.debug("set monster item {} for monster gear: {}", item.getId(), domain.getId());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            }).thenReturn(domain);
                });
    }

    @Override
    public Mono<MonsterGear> removeMonsterItem(MonsterGearCommand.RemoveMonsterItemCommand command) {
        logger.debug("removing item to monster: {}", command.getMonsterId());
        return monsterGearDomainRepository.fetchByMonsterId(command.getMonsterId())
                .doOnError(e -> logger.error("exception occurred while removing item to monster: {}, exception: {}", command.getMonsterId(), e.getMessage()))
                .flatMap(domain -> {
                    var item = domain.removeItem(command.getSlot());
                    var event = new MonsterGearEvent.MonsterItemRemovedEvent(
                            domain.getId(),
                            domain.getMonsterId(),
                            item.getId(),
                            command.getSlot()
                    );
                    return mongoTemplate.save(domain)
                            .flatMap(unused -> Mono.fromFuture(kafkaTemplate.send(topicNames.getMonsterItemRemoved().getName(), domain.getId(), event)).thenReturn(unused))
                            .doOnSuccess(unused -> {
                                logger.debug("removed monster item {} for monster gear: {}", item.getId(), domain.getId());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            }).thenReturn(domain);
                });
    }
}
