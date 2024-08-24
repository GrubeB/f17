package pl.app.god_equipment.application;

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
import pl.app.god_equipment.application.domain.CharacterGear;
import pl.app.god_equipment.application.domain.GodEquipmentEvent;
import pl.app.god_equipment.application.domain.GodEquipmentException;
import pl.app.god_equipment.application.port.in.CharacterGearCommand;
import pl.app.god_equipment.application.port.in.CharacterGearService;
import pl.app.god_equipment.application.port.out.GodEquipmentDomainRepository;
import reactor.core.publisher.Mono;


@Service
@RequiredArgsConstructor
class CharacterGearServiceImpl implements CharacterGearService {
    private static final Logger logger = LoggerFactory.getLogger(CharacterGearServiceImpl.class);

    private final ReactiveMongoTemplate mongoTemplate;
    private final KafkaTemplate<ObjectId, Object> kafkaTemplate;
    private final KafkaTopicConfigurationProperties topicNames;
    private final GodEquipmentDomainRepository domainRepository;

    @Override
    public Mono<CharacterGear> crateCharacterGear(CharacterGearCommand.CreateCharacterGearCommand command) {
        logger.debug("creating character gear for character: {}", command.getCharacterId());
        return mongoTemplate.exists(Query.query(Criteria.where("characterId").is(command.getCharacterId())), CharacterGear.class)
                .flatMap(exist -> exist ? Mono.error(GodEquipmentException.DuplicatedCharacterGearException.fromId(command.getCharacterId().toHexString())) : Mono.empty())
                .doOnError(e -> logger.error("exception occurred while creating  character gear for character: {}, exception: {}", command.getCharacterId(), e.getMessage()))
                .then(Mono.defer(() -> {
                    CharacterGear domain = new CharacterGear(command.getCharacterId());
                    var event = new GodEquipmentEvent.CharacterGearRemovedEvent(
                            domain.getId(),
                            domain.getCharacterId()
                    );
                    return mongoTemplate.insert(domain)
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getCharacterGearCreated().getName(), saved.getId(), event)).thenReturn(saved))
                            .doOnSuccess(saved -> {
                                logger.debug("created character gear {} for character: {}", saved.getId(), saved.getCharacterId());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            });
                }));
    }

    @Override
    public Mono<CharacterGear> removeCharacterGear(CharacterGearCommand.RemoveCharacterGearCommand command) {
        logger.debug("removing character gear for character: {}", command.getCharacterId());
        return domainRepository.fetchCharacterGearByCharacterId(command.getCharacterId())
                .doOnError(e -> logger.error("exception occurred while removing  character gear for character: {}, exception: {}", command.getCharacterId(), e.getMessage()))
                .flatMap(domain -> {

                    var event = new GodEquipmentEvent.CharacterGearRemovedEvent(
                            domain.getId(),
                            domain.getCharacterId()
                    );
                    return mongoTemplate.remove(domain)
                            .flatMap(unused -> Mono.fromFuture(kafkaTemplate.send(topicNames.getCharacterGearRemoved().getName(), domain.getId(), event)).thenReturn(unused))
                            .doOnSuccess(unused -> {
                                logger.debug("removed character gear {} for character: {}", domain.getId(), domain.getCharacterId());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            }).thenReturn(domain);
                });
    }
}
