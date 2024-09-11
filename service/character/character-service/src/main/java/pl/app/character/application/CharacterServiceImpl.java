package pl.app.character.application;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import pl.app.character.application.domain.Character;
import pl.app.character.application.domain.CharacterEvent;
import pl.app.character.application.port.in.CharacterCommand;
import pl.app.character.application.port.in.CharacterService;
import pl.app.character.application.port.out.CharacterDomainRepository;
import pl.app.character.application.port.out.CharacterTemplateRepository;
import pl.app.common.shared.model.StatisticType;
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
    private final CharacterTemplateRepository characterTemplateRepository;

    @Override
    public Mono<Character> createCharacter(CharacterCommand.CreateCharacterCommand command) {
        logger.debug("creating character: {}", command.getTemplateId());
        return characterTemplateRepository.fetchById(command.getTemplateId())
                .doOnError(e -> logger.error("exception occurred while creating character: {}, exception: {}", command.getTemplateId(), e.getMessage()))
                .flatMap(template -> {
                    var domain = new Character(template);
                    var event = new CharacterEvent.CharacterCreatedEvent(
                            domain.getId(),
                            domain.getName(),
                            domain.getProfession().name()
                    );
                    return mongoTemplate.insert(domain)
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getCharacterCreated().getName(), saved.getId(), event)).thenReturn(saved))
                            .doOnSuccess(saved -> {
                                logger.debug("created character: {}", saved.getId());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            });
                });
    }

    @Override
    public Mono<Character> createRandomCharacter(CharacterCommand.CreateRandomCharacterCommand command) {
        logger.debug("creating random character");
        return characterTemplateRepository.fetchRandomTemplate()
                .doOnError(e -> logger.error("exception occurred while creating random character, exception: {}", e.getMessage()))
                .flatMap(template -> {
                    var domain = new Character(template);
                    var event = new CharacterEvent.CharacterCreatedEvent(
                            domain.getId(),
                            domain.getName(),
                            domain.getProfession().name()
                    );
                    return mongoTemplate.insert(domain)
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getCharacterCreated().getName(), saved.getId(), event)).thenReturn(saved))
                            .doOnSuccess(saved -> {
                                logger.debug("created character: {}", saved.getId());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            });
                });
    }

    @Override
    public Mono<Character> removeCharacter(CharacterCommand.RemoveCharacterCommand command) {
        logger.debug("removing character: {}", command.getCharacterId());
        return domainRepository.fetchById(command.getCharacterId())
                .doOnError(e -> logger.error("exception occurred while removing character: {}, exception: {}", command.getCharacterId(), e.getMessage()))
                .flatMap(character -> {
                    var event = new CharacterEvent.CharacterRemovedEvent(
                            character.getId()
                    );
                    return mongoTemplate.remove(character)
                            .then(Mono.defer(() -> Mono.fromFuture(kafkaTemplate.send(topicNames.getCharacterRemoved().getName(), character.getId(), event)).thenReturn(character)))
                            .doOnSuccess(removed -> {
                                logger.debug("removed character: {}", removed.getId());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            });
                });
    }

    @Override
    public Mono<Character> addStatistic(CharacterCommand.AddStatisticCommand command) {
        logger.debug("adding statistic to character: {}", command.getCharacterId());
        return domainRepository.fetchById(command.getCharacterId())
                .doOnError(e -> logger.error("exception occurred while adding statistic to character: {}, exception: {}", command.getCharacterId(), e.getMessage()))
                .flatMap(character -> {
                    character.addStatistics(command.getStatisticQuantity(), StatisticType.valueOf(command.getStatisticName()));
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
