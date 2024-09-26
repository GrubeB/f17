package pl.app.family.application;

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
import pl.app.family.application.domain.Family;
import pl.app.family.application.domain.FamilyEvent;
import pl.app.family.application.domain.FamilyException;
import pl.app.family.application.port.in.FamilyCommand;
import pl.app.family.application.port.in.FamilyService;
import pl.app.family.application.port.out.CharacterDomainRepository;
import pl.app.family.application.port.out.FamilyDomainRepository;
import reactor.core.publisher.Mono;


@Service
@RequiredArgsConstructor
class FamilyServiceImpl implements FamilyService {
    private static final Logger logger = LoggerFactory.getLogger(FamilyServiceImpl.class);

    private final ReactiveMongoTemplate mongoTemplate;
    private final KafkaTemplate<ObjectId, Object> kafkaTemplate;
    private final KafkaTopicConfigurationProperties topicNames;
    private final CharacterDomainRepository characterDomainRepository;
    private final FamilyDomainRepository familyDomainRepository;

    @Override
    public Mono<Family> create(FamilyCommand.CreateFamilyCommand command) {
        logger.debug("creating god family for god: {}", command.getGodId());
        return mongoTemplate.exists(Query.query(Criteria.where("godId").is(command.getGodId())), Family.class)
                .flatMap(exist -> exist ? Mono.error(FamilyException.DuplicatedGodsException.fromGodId(command.getGodId().toHexString())) : Mono.empty())
                .doOnError(e -> logger.error("exception occurred while creating god family for god: {}, exception: {}", command.getGodId(), e.getMessage()))
                .then(Mono.defer(() -> {
                    Family domain = new Family(command.getGodId());
                    var event = new FamilyEvent.FamilyCreatedEvent(
                            domain.getId(),
                            domain.getGodId()
                    );
                    return mongoTemplate.insert(domain)
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getFamilyCreated().getName(), saved.getId(), event)).thenReturn(saved))
                            .doOnSuccess(saved -> {
                                logger.debug("created god family: {}, for god: {}", saved.getId(), saved.getGodId());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            });
                }));
    }

    @Override
    public Mono<Family> add(FamilyCommand.AddCharacterToFamilyCommand command) {
        logger.debug("adding to god family of god: {}, character {}", command.getGodId(), command.getCharacterId());
        return mongoTemplate.exists(Query.query(Criteria.where("characters._id").is(command.getCharacterId())), Family.class)
                .flatMap(exist -> exist ? Mono.error(FamilyException.CharacterBelongsToFamilyException.fromId(command.getCharacterId().toHexString())) : Mono.empty())
                .then(familyDomainRepository.fetchByGodId(command.getGodId()))
                .zipWith(characterDomainRepository.fetchById(command.getCharacterId()))
                .doOnError(e -> logger.error("exception occurred while adding to god family of god: {}, character {}, exception: {}", command.getGodId(), command.getCharacterId(), e.getMessage()))
                .flatMap(tuple2 -> {
                    Family domain = tuple2.getT1();
                    Character character = tuple2.getT2();
                    domain.addCharacter(character);
                    var event = new FamilyEvent.CharacterAddedToFamilyEvent(
                            domain.getId(),
                            domain.getGodId(),
                            character.getId()
                    );
                    return mongoTemplate.save(domain)
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getCharacterAddedToFamily().getName(), saved.getId(), event)).thenReturn(saved))
                            .doOnSuccess(saved -> {
                                logger.debug("added to god family of god: {}, character: {}", saved.getId(), command.getCharacterId());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            });
                });
    }

    @Override
    public Mono<Family> remove(FamilyCommand.RemoveCharacterFromFamilyCommand command) {
        logger.debug("removing from god family of god: {}, character {}", command.getGodId(), command.getCharacterId());
        return familyDomainRepository.fetchByGodId(command.getGodId())
                .doOnError(e -> logger.error("exception occurred while removing from god family of god: {}, character {}, exception: {}", command.getGodId(), command.getCharacterId(), e.getMessage()))
                .flatMap(domain -> {
                    Character character = domain.removeCharacterById(command.getCharacterId());
                    var event = new FamilyEvent.CharacterRemovedFromFamilyEvent(
                            domain.getId(),
                            domain.getGodId(),
                            character.getId()
                    );
                    return mongoTemplate.save(domain)
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getCharacterRemovedFromFamily().getName(), saved.getId(), event)).thenReturn(saved))
                            .doOnSuccess(saved -> {
                                logger.debug("removed from god family of god: {}, character: {}", saved.getId(), command.getCharacterId());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            });
                });
    }
}
