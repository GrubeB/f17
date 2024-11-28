package pl.app.army.village_army.application.port.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import pl.app.army.unit.model.Army;
import pl.app.army.unit.model.Unit;
import pl.app.army.unit.model.UnitType;
import pl.app.army.unit.service.UnitService;
import pl.app.army.village_army.application.domain.VillageArmy;
import pl.app.army.village_army.application.domain.VillageArmyEvent;
import pl.app.army.village_army.application.domain.VillageArmyException;
import pl.app.config.KafkaTopicConfigurationProperties;
import pl.app.resource.share.Resource;
import pl.app.resource.share.ResourceType;
import pl.app.resource.village_resource.application.port.in.VillageResourceCommand;
import pl.app.resource.village_resource.application.port.in.VillageResourceService;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.function.Function;


@Service
@RequiredArgsConstructor
class VillageArmyServiceImpl implements VillageArmyService {
    private static final Logger logger = LoggerFactory.getLogger(VillageArmyServiceImpl.class);

    private final ReactiveMongoTemplate mongoTemplate;
    private final KafkaTemplate<ObjectId, Object> kafkaTemplate;
    private final KafkaTopicConfigurationProperties topicNames;

    private final VillageArmyDomainRepository villageArmyDomainRepository;
    private final VillageResourceService villageResourceService;
    private final UnitService unitService;

    @Override
    public Mono<VillageArmy> crate(VillageArmyCommand.CreateVillageArmyCommand command) {
        return Mono.fromCallable(() ->
                mongoTemplate.exists(Query.query(Criteria.where("villageId").is(command.getVillageId().toHexString())), VillageArmy.class)
                        .flatMap(exist -> exist ? Mono.error(VillageArmyException.DuplicatedVillageArmyException.fromId(command.getVillageId().toHexString())) : Mono.empty())
                        .then(Mono.defer(() -> {
                            var domain = new VillageArmy(command.getVillageId());
                            var event = new VillageArmyEvent.VillageArmyCreatedEvent(domain.getVillageId());
                            return mongoTemplate.insert(domain)
                                    .then(Mono.fromFuture(kafkaTemplate.send(topicNames.getVillageArmyCreated().getName(), domain.getVillageId(), event)))
                                    .thenReturn(domain);
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
                            Army army = Army.of(command.getArmy());
                            domain.add(army);
                            var event = new VillageArmyEvent.UnitsAddedEvent(domain.getVillageId(), army);
                            return mongoTemplate.save(domain)
                                    .then(Mono.fromFuture(kafkaTemplate.send(topicNames.getUnitsAdded().getName(), domain.getVillageId(), event)))
                                    .thenReturn(domain);
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
                            Army army = Army.of(command.getArmy());
                            domain.subtract(army);
                            var event = new VillageArmyEvent.UnitsSubtractedEvent(domain.getVillageId(), army);
                            return mongoTemplate.save(domain)
                                    .then(Mono.fromFuture(kafkaTemplate.send(topicNames.getUnitsAdded().getName(), domain.getVillageId(), event)))
                                    .thenReturn(domain);
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
                            Army army = Army.of(command.getArmy());
                            domain.block(army);
                            var event = new VillageArmyEvent.UnitsBlockedEvent(domain.getVillageId(), army);
                            return mongoTemplate.save(domain)
                                    .then(Mono.fromFuture(kafkaTemplate.send(topicNames.getUnitsBlocked().getName(), domain.getVillageId(), event)))
                                    .thenReturn(domain);
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
    public Mono<VillageArmy> unlock(VillageArmyCommand.UnlockUnitsCommand command) {
        return Mono.fromCallable(() ->
                villageArmyDomainRepository.fetchByVillageId(command.getVillageId())
                        .flatMap(domain -> {
                            Army army = Army.of(command.getArmy());
                            domain.unblock(army);
                            var event = new VillageArmyEvent.UnitsUnlockedEvent(domain.getVillageId(), army);
                            return mongoTemplate.save(domain)
                                    .then(Mono.fromFuture(kafkaTemplate.send(topicNames.getUnitsUnlocked().getName(), domain.getVillageId(), event)))
                                    .thenReturn(domain);
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
                            Army army = Army.of(command.getArmy());
                            domain.addSupport(command.getSupportingVillageId(), army);
                            var event = new VillageArmyEvent.VillageSupportAddedEvent(domain.getVillageId(), command.getSupportingVillageId(), army);
                            return mongoTemplate.save(domain)
                                    .then(Mono.fromFuture(kafkaTemplate.send(topicNames.getVillageSupportAdded().getName(), domain.getVillageId(), event)))
                                    .thenReturn(domain);
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
    public Mono<VillageArmy> withdrawSupport(VillageArmyCommand.WithdrawVillageSupportCommand command) {
        return Mono.fromCallable(() ->
                villageArmyDomainRepository.fetchByVillageId(command.getSupportedVillageId())
                        .flatMap(domain -> {
                            Army army = Army.of(command.getArmy());
                            domain.removeSupport(command.getSupportingVillageId(), army);
                            var event = new VillageArmyEvent.VillageSupportWithdrawEvent(domain.getVillageId(), command.getSupportingVillageId(), army);
                            return mongoTemplate.save(domain)
                                    .then(Mono.fromFuture(kafkaTemplate.send(topicNames.getVillageSupportWithdraw().getName(), domain.getVillageId(), event)))
                                    .thenReturn(domain);
                        })
        ).doOnSubscribe(subscription ->
                logger.debug("subtracting support in village's army: {}", command.getSupportedVillageId())
        ).flatMap(Function.identity()).doOnSuccess(saved ->
                logger.debug("subtracted support in village's army: {}", saved.getVillageId())
        ).doOnError(e ->
                logger.error("exception occurred while subtracting support in village's army: {}, exception: {}", command.getSupportedVillageId(), e.toString())
        );
    }

    @Override
    public Mono<VillageArmy> removeAllUnitsFromVillage(VillageArmyCommand.RemoveAllUnitsFromVillageCommand command) {
        return Mono.fromCallable(() ->
                villageArmyDomainRepository.fetchByVillageId(command.getVillageId())
                        .zipWith(unitService.fetchAll())
                        .flatMap(t -> {
                            VillageArmy domain = t.getT1();
                            Army removedUnits = domain.reset();
                            Integer provision = calculateNumberOfProvisions(removedUnits, t.getT2());
                            return villageResourceService
                                    .add(new VillageResourceCommand.AddResourceCommand(domain.getVillageId(), Resource.of(provision, ResourceType.PROVISION)))
                                    .then(mongoTemplate.save(domain));
                        })
        ).doOnSubscribe(subscription ->
                logger.debug("removing all units from village: {}", command.getVillageId())
        ).flatMap(Function.identity()).doOnSuccess(saved ->
                logger.debug("removed all units from village: {}", saved.getVillageId())
        ).doOnError(e ->
                logger.error("exception occurred while removing all units from village: {}, exception: {}", command.getVillageId(), e.toString())
        );
    }

    private Integer calculateNumberOfProvisions(Army removedUnits, Map<UnitType, Unit> t2) {
        return removedUnits.entrySet().stream()
                .mapToInt(e -> t2.get(e.getKey()).getCost().getProvision() * e.getValue())
                .sum();
    }
}
