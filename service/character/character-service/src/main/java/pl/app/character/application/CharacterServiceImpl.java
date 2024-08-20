package pl.app.character.application;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import pl.app.character.application.domain.CharacterEvent;
import pl.app.character.application.domain.CharacterException;
import pl.app.character.application.domain.Character;
import pl.app.character.application.port.in.CharacterCommand;
import pl.app.character.application.port.in.CharacterService;
import pl.app.character.application.port.out.CharacterDomainRepository;
import pl.app.config.KafkaTopicConfigurationProperties;
import reactor.core.publisher.Mono;


@Service
@RequiredArgsConstructor
class CharacterServiceImpl implements CharacterService {
    private static final Logger logger = LoggerFactory.getLogger(CharacterServiceImpl.class);

    private final CharacterDomainRepository domainRepository;
    private final ReactiveMongoTemplate mongoTemplate;
    private final KafkaTemplate<ObjectId, Object> kafkaTemplate;
    private final KafkaTopicConfigurationProperties topicNames;

    @Override
    public Mono<Character> createCharacter(CharacterCommand.CreateCharacterCommand command) {
        logger.debug("creating character: {}", command.getName());
        return Mono.when(verifyThereIsNoDuplicatedName(command.getName()))
                .doOnError(e -> logger.error("exception occurred while creating character: {}, exception: {}", command.getName(), e.getMessage()))
                .then(Mono.defer(() -> {
                    Character character = new Character(command.getName(), command.getProfession());
                    var event = new CharacterEvent.CharacterCreatedEvent(
                            character.getId(),
                            character.getName(),
                            character.getProfession().name()
                    );
                    return mongoTemplate.insert(character)
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getCharacterCreated().getName(), saved.getId(), event)).thenReturn(saved))
                            .doOnSuccess(saved -> {
                                logger.debug("created character: {}", command.getName());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            });
                }));
    }

    private Mono<Void> verifyThereIsNoDuplicatedName(String name) {
        Query query = Query.query(Criteria
                .where("name").is(name)
        );
        return mongoTemplate.exists(query, Character.class)
                .flatMap(exist -> exist ? Mono.error(CharacterException.DuplicatedNameException.fromName(name)) : Mono.empty());
    }

    @Override
    public Mono<Character> addStatistic(CharacterCommand.AddStatisticCommand command) {
        logger.debug("adding statistic to character: {}", command.getCharacterId());
        return domainRepository.fetchById(command.getCharacterId())
                .doOnError(e -> logger.error("exception occurred while adding statistic to character: {}, exception: {}", command.getCharacterId(), e.getMessage()))
                .flatMap(character -> {
                    character.getStatistics().addStatistic(command.getStatisticQuantity(), command.getStatisticName());
                    var event = new CharacterEvent.StatisticAddedEvent(
                            character.getId(),
                            command.getStatisticName(),
                            command.getStatisticQuantity()
                    );
                    return mongoTemplate.save(character)
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getStatisticAdded().getName(), saved.getId(), event)).thenReturn(saved))
                            .doOnSuccess(saved -> {
                                logger.debug("added statistic to character: {}, statistic: {}, quantity: {}", command.getCharacterId(), command.getStatisticName(), command.getStatisticQuantity());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            });
                });
    }
}
