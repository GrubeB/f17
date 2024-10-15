package pl.app.map.village_position.application.port.in;

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
import pl.app.map.map.application.port.in.MapDomainRepository;
import pl.app.map.village_position.application.domain.VillagePosition;
import pl.app.map.village_position.application.domain.VillagePositionException;
import pl.app.map.village_position.application.domain.VillagePositionProvider;
import reactor.core.publisher.Mono;


@Service
@RequiredArgsConstructor
class VillagePositionServiceImpl implements VillagePositionService {
    private static final Logger logger = LoggerFactory.getLogger(VillagePositionServiceImpl.class);

    private final ReactiveMongoTemplate mongoTemplate;
    private final KafkaTemplate<ObjectId, Object> kafkaTemplate;
    private final KafkaTopicConfigurationProperties topicNames;
    private final MapDomainRepository mapDomainRepository;
    private final VillagePositionProvider villagePositionProvider;

    @Override
    public Mono<VillagePosition> crate(VillagePositionCommand.CreateVillagePositionCommand command) {
        logger.debug("crating village position: {}", command.getVillageId());
        return mongoTemplate.exists(Query.query(Criteria.where("villageId").in(command.getVillageId().toHexString())), VillagePosition.class)
                .flatMap(exist -> exist ? Mono.error(VillagePositionException.DuplicatedVillagePositionException.fromId(command.getVillageId().toHexString())) : Mono.empty())
                .doOnError(e -> logger.error("exception occurred while crating village position: {}, exception: {}", command.getVillageId(), e.getMessage()))
                .then(villagePositionProvider.getNewVillagePosition())
                .flatMap(newVillagePosition -> {
                    var domain = new VillagePosition(newVillagePosition, command.getVillageId());
                    return mongoTemplate.insert(domain)
                            .doOnSuccess(saved -> logger.debug("created village position: {}", saved.getVillageId()));
                });
    }
}
