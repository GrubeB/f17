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
        logger.debug("crating village army: {}", command.getVillageId());
        return mongoTemplate.exists(Query.query(Criteria.where("villageId").is(command.getVillageId().toHexString())), VillageArmy.class)
                .flatMap(exist -> exist ? Mono.error(VillageArmyException.DuplicatedVillageArmyException.fromId(command.getVillageId().toHexString())) : Mono.empty())
                .doOnError(e -> logger.error("exception occurred while crating village army: {}, exception: {}", command.getVillageId(), e.getMessage()))
                .then(Mono.defer(() -> {
                    var domain = new VillageArmy(command.getVillageId());
                    var event = new VillageArmyEvent.VillageArmyCreatedEvent(domain.getVillageId());
                    return mongoTemplate.insert(domain)
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getVillageArmyCreated().getName(), saved.getVillageId(), event)).thenReturn(saved))
                            .doOnSuccess(saved -> logger.debug("created village army: {}", saved.getVillageId()));
                }));
    }

    @Override
    public Mono<VillageArmy> add(VillageArmyCommand.AddUnitsCommand command) {
        logger.debug("adding units to village army: {}", command.getVillageId());
        return villageArmyDomainRepository.fetchByVillageId(command.getVillageId())
                .doOnError(e -> logger.error("exception occurred while adding units to village army: {}, exception: {}", command.getVillageId(), e.getMessage()))
                .flatMap(domain -> {
                    domain.add(command.getType(), command.getAmount());
                    var event = new VillageArmyEvent.UnitsAddedEvent(domain.getVillageId(), command.getType(), command.getAmount());
                    return mongoTemplate.save(domain)
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getUnitsAdded().getName(), saved.getVillageId(), event)).thenReturn(saved))
                            .doOnSuccess(saved -> logger.debug("added units to village army: {}", saved.getVillageId()));
                });
    }

    @Override
    public Mono<VillageArmy> subtract(VillageArmyCommand.SubtractUnitsCommand command) {
        logger.debug("subtracting units to village army: {}", command.getVillageId());
        return villageArmyDomainRepository.fetchByVillageId(command.getVillageId())
                .doOnError(e -> logger.error("exception occurred while subtracting units to village army: {}, exception: {}", command.getVillageId(), e.getMessage()))
                .flatMap(domain -> {
                    domain.subtract(command.getType(), command.getAmount());
                    var event = new VillageArmyEvent.UnitsAddedEvent(domain.getVillageId(), command.getType(), command.getAmount());
                    return mongoTemplate.save(domain)
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getUnitsAdded().getName(), saved.getVillageId(), event)).thenReturn(saved))
                            .doOnSuccess(saved -> logger.debug("subtracted units to village army: {}", saved.getVillageId()));
                });
    }
}
