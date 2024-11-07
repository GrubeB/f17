package pl.app.unit.village_army.application.port.in;

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
import pl.app.unit.village_army.application.domain.VillageArmy;
import pl.app.unit.village_army.application.domain.VillageArmyEvent;
import pl.app.unit.village_army.application.domain.VillageArmyException;
import reactor.core.publisher.Mono;

import java.util.function.Function;


@Service
@RequiredArgsConstructor
class VillageArmyServiceImpl implements VillageArmyService {
    private static final Logger logger = LoggerFactory.getLogger(VillageArmyServiceImpl.class);

    private final ReactiveMongoTemplate mongoTemplate;
    private final KafkaTemplate<ObjectId, Object> kafkaTemplate;
    private final KafkaTopicConfigurationProperties topicNames;

    private final VillageArmyDomainRepository villageArmyDomainRepository;

    @Override
    public Mono<VillageArmy> crate(VillageArmyCommand.CreateVillageArmyCommand command) {
        return Mono.fromCallable(() ->
                mongoTemplate.exists(Query.query(Criteria.where("villageId").is(command.getVillageId().toHexString())), VillageArmy.class)
                        .flatMap(exist -> exist ? Mono.error(VillageArmyException.DuplicatedVillageArmyException.fromId(command.getVillageId().toHexString())) : Mono.empty())
                        .then(Mono.defer(() -> {
                            var domain = new VillageArmy(command.getVillageId());
                            var event = new VillageArmyEvent.VillageArmyCreatedEvent(domain.getVillageId());
                            return mongoTemplate.insert(domain)
                                    .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getVillageArmyCreated().getName(), saved.getVillageId(), event)).thenReturn(saved));
                        }))
        ).doOnSubscribe(subscription ->
                logger.debug("crating village army for village: {}", command.getVillageId())
        ).flatMap(Function.identity()).doOnSuccess(saved ->
                logger.debug("created village army for village: {}", saved.getVillageId())
        ).doOnError(e ->
                logger.error("exception occurred while crating village army for village: {}, exception: {}", command.getVillageId(), e.toString())
        );
    }

    @Override
    public Mono<VillageArmy> add(VillageArmyCommand.AddUnitsCommand command) {
        return Mono.fromCallable(() ->
                villageArmyDomainRepository.fetchByVillageId(command.getVillageId())
                        .flatMap(domain -> {
                            domain.add(command.getArmy());
                            var event = new VillageArmyEvent.UnitsAddedEvent(domain.getVillageId(), command.getArmy());
                            return mongoTemplate.save(domain)
                                    .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getUnitsAdded().getName(), saved.getVillageId(), event)).thenReturn(saved));
                        })
        ).doOnSubscribe(subscription ->
                logger.debug("adding units to village's army: {}", command.getVillageId())
        ).flatMap(Function.identity()).doOnSuccess(saved ->
                logger.debug("added units to village's army: {}", saved.getVillageId())
        ).doOnError(e ->
                logger.error("exception occurred while adding units to village's army: {}, exception: {}", command.getVillageId(), e.toString())
        );
    }

    @Override
    public Mono<VillageArmy> subtract(VillageArmyCommand.SubtractUnitsCommand command) {
        return Mono.fromCallable(() ->
                villageArmyDomainRepository.fetchByVillageId(command.getVillageId())
                        .flatMap(domain -> {
                            domain.subtract(command.getArmy());
                            var event = new VillageArmyEvent.UnitsSubtractedEvent(domain.getVillageId(), command.getArmy());
                            return mongoTemplate.save(domain)
                                    .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getUnitsAdded().getName(), saved.getVillageId(), event)).thenReturn(saved));
                        })
        ).doOnSubscribe(subscription ->
                logger.debug("subtracting units in village's army: {}", command.getVillageId())
        ).flatMap(Function.identity()).doOnSuccess(saved ->
                logger.debug("subtracted units in village's army: {}", saved.getVillageId())
        ).doOnError(e ->
                logger.error("exception occurred while subtracting units in village's army: {}, exception: {}", command.getVillageId(), e.toString())
        );
    }

    @Override
    public Mono<VillageArmy> block(VillageArmyCommand.BlockUnitsCommand command) {
        return Mono.fromCallable(() ->
                villageArmyDomainRepository.fetchByVillageId(command.getVillageId())
                        .flatMap(domain -> {
                            domain.block(command.getArmy());
                            return mongoTemplate.save(domain);
                        })
        ).doOnSubscribe(subscription ->
                logger.debug("blocking units in village's army: {}", command.getVillageId())
        ).flatMap(Function.identity()).doOnSuccess(saved ->
                logger.debug("blocked units in village's army: {}", saved.getVillageId())
        ).doOnError(e ->
                logger.error("exception occurred while blocking units in village's army: {}, exception: {}", command.getVillageId(), e.toString())
        );
    }

    @Override
    public Mono<VillageArmy> unblock(VillageArmyCommand.UnblockUnitsCommand command) {
        return Mono.fromCallable(() ->
                villageArmyDomainRepository.fetchByVillageId(command.getVillageId())
                        .flatMap(domain -> {
                            domain.unblock(command.getArmy());
                            return mongoTemplate.save(domain);
                        })
        ).doOnSubscribe(subscription ->
                logger.debug("unblocking units in village's army: {}", command.getVillageId())
        ).flatMap(Function.identity()).doOnSuccess(saved ->
                logger.debug("unblocked units in village's army: {}", saved.getVillageId())
        ).doOnError(e ->
                logger.error("exception occurred while unblocking units in village's army: {}, exception: {}", command.getVillageId(), e.toString())
        );
    }

    @Override
    public Mono<VillageArmy> addSupport(VillageArmyCommand.AddVillageSupportCommand command) {
        return Mono.fromCallable(() ->
                villageArmyDomainRepository.fetchByVillageId(command.getSupportedVillageId())
                        .flatMap(domain -> {
                            domain.addSupport(command.getSupportingVillageId(), command.getArmy());
                            return mongoTemplate.save(domain);
                        })
        ).doOnSubscribe(subscription ->
                logger.debug("adding support in village's army: {}", command.getSupportedVillageId())
        ).flatMap(Function.identity()).doOnSuccess(saved ->
                logger.debug("added support in village's army: {}", saved.getVillageId())
        ).doOnError(e ->
                logger.error("exception occurred while adding support in village's army: {}, exception: {}", command.getSupportedVillageId(), e.toString())
        );
    }

    @Override
    public Mono<VillageArmy> subtractSupport(VillageArmyCommand.SubtractVillageSupportCommand command) {
        return Mono.fromCallable(() ->
                villageArmyDomainRepository.fetchByVillageId(command.getSupportedVillageId())
                        .flatMap(domain -> {
                            domain.removeSupport(command.getSupportingVillageId(), command.getArmy());
                            return mongoTemplate.save(domain);
                        })
        ).doOnSubscribe(subscription ->
                logger.debug("subtracting support in village's army: {}", command.getSupportedVillageId())
        ).flatMap(Function.identity()).doOnSuccess(saved ->
                logger.debug("subtracted support in village's army: {}", saved.getVillageId())
        ).doOnError(e ->
                logger.error("exception occurred while subtracting support in village's army: {}, exception: {}", command.getSupportedVillageId(), e.toString())
        );
    }
}
