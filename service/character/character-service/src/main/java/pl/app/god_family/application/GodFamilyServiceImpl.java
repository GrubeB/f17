package pl.app.god_family.application;

import god_family.application.domain.GodFamily;
import god_family.application.domain.GodFamilyEvent;
import god_family.application.domain.GodFamilyException;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import pl.app.character.application.domain.Character;
import pl.app.config.KafkaTopicConfigurationProperties;
import pl.app.god_family.application.port.in.GodFamilyCommand;
import pl.app.god_family.application.port.in.GodFamilyService;
import pl.app.god_family.application.port.out.CharacterDomainRepository;
import pl.app.god_family.application.port.out.GodFamilyDomainRepository;
import reactor.core.publisher.Mono;


@Service
@RequiredArgsConstructor
class GodFamilyServiceImpl implements GodFamilyService {
    private static final Logger logger = LoggerFactory.getLogger(GodFamilyServiceImpl.class);

    private final ReactiveMongoTemplate mongoTemplate;
    private final KafkaTemplate<ObjectId, Object> kafkaTemplate;
    private final KafkaTopicConfigurationProperties topicNames;
    private final CharacterDomainRepository characterDomainRepository;
    private final GodFamilyDomainRepository godFamilyDomainRepository;

    @Override
    public Mono<GodFamily> create(GodFamilyCommand.CreateGodFamilyCommand command) {
        logger.debug("creating god family for god: {}", command.getGodId());
        return mongoTemplate.exists(Query.query(Criteria.where("godId").is(command.getGodId())), GodFamily.class)
                .flatMap(exist -> exist ? Mono.error(GodFamilyException.DuplicatedGodsException.fromGodId(command.getGodId().toHexString())) : Mono.empty())
                .doOnError(e -> logger.error("exception occurred while creating god family for god: {}, exception: {}", command.getGodId(), e.getMessage()))
                .then(Mono.defer(() -> {
                    GodFamily domain = new GodFamily(command.getGodId());
                    var event = new GodFamilyEvent.GodFamilyCreatedEvent(
                            domain.getId(),
                            domain.getGodId()
                    );
                    return mongoTemplate.insert(domain)
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getGodFamilyCreated().getName(), saved.getId(), event)).thenReturn(saved))
                            .doOnSuccess(saved -> {
                                logger.debug("created god family: {}, for god: {}", saved.getId(), saved.getGodId());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            });
                }));
    }

    @Override
    public Mono<GodFamily> add(GodFamilyCommand.AddCharacterToGodFamilyCommand command) {
        logger.debug("adding to god family of god: {}, character {}", command.getGodId(), command.getCharacterId());
        return godFamilyDomainRepository.fetchByGodId(command.getGodId())
                .zipWith(characterDomainRepository.fetchById(command.getCharacterId()))
                .doOnError(e -> logger.error("exception occurred while adding to god family of god: {}, character {}, exception: {}", command.getGodId(), command.getCharacterId(), e.getMessage()))
                .flatMap(tuple2 -> {
                    GodFamily domain = tuple2.getT1();
                    Character character = tuple2.getT2();
                    domain.addCharacter(character);
                    var event = new GodFamilyEvent.CharacterAddedToFamilyEvent(
                            domain.getId(),
                            domain.getGodId(),
                            character.getId()
                    );
                    return mongoTemplate.save(domain)
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getCharacterAddedToGodFamily().getName(), saved.getId(), event)).thenReturn(saved))
                            .doOnSuccess(saved -> {
                                logger.debug("added to god family of god: {}, character: {}", saved.getId(), command.getCharacterId());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            });
                });
    }

    @Override
    public Mono<GodFamily> remove(GodFamilyCommand.RemoveCharacterFromGodFamilyCommand command) {
        logger.debug("removing from god family of god: {}, character {}", command.getGodId(), command.getCharacterId());
        return godFamilyDomainRepository.fetchByGodId(command.getGodId())
                .doOnError(e -> logger.error("exception occurred while removing from god family of god: {}, character {}, exception: {}", command.getGodId(), command.getCharacterId(), e.getMessage()))
                .flatMap(domain -> {
                    Character character = domain.removeCharacterById(command.getCharacterId());
                    var event = new GodFamilyEvent.CharacterRemovedFromFamilyEvent(
                            domain.getId(),
                            domain.getGodId(),
                            character.getId()
                    );
                    return mongoTemplate.save(domain)
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getCharacterRemovedFromGodFamily().getName(), saved.getId(), event)).thenReturn(saved))
                            .doOnSuccess(saved -> {
                                logger.debug("removed from god family of god: {}, character: {}", saved.getId(), command.getCharacterId());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            });
                });
    }
}
