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
import pl.app.character.application.port.in.CharacterLevelService;
import pl.app.character.application.port.out.CharacterDomainRepository;
import pl.app.config.KafkaTopicConfigurationProperties;
import reactor.core.publisher.Mono;


@Service
@RequiredArgsConstructor
class CharacterLevelServiceImpl implements CharacterLevelService {
    private static final Logger logger = LoggerFactory.getLogger(CharacterLevelServiceImpl.class);

    private final CharacterDomainRepository domainRepository;
    private final ReactiveMongoTemplate mongoTemplate;
    private final KafkaTemplate<ObjectId, Object> kafkaTemplate;
    private final KafkaTopicConfigurationProperties topicNames;

    @Override
    public Mono<Character> addExp(CharacterCommand.AddExpCommand command) {
        logger.debug("adding {} exp to character: {}", command.getAmount(), command.getCharacterId());
        return domainRepository.fetchById(command.getCharacterId())
                .doOnError(e -> logger.error("exception occurred while adding {} exp to character: {}, exception: {}", command.getAmount(), command.getCharacterId(), e.getMessage()))
                .flatMap(character -> {
                    Integer numberOfLevelIncreased = character.addExp(command.getAmount());
                    var event = new CharacterEvent.ExpAddedEvent(
                            character.getId(),
                            command.getAmount()
                    );
                    return mongoTemplate.save(character)
                            .flatMap(saved -> Mono.when(
                                    Mono.fromFuture(kafkaTemplate.send(topicNames.getExpAdded().getName(), saved.getId(), event)).thenReturn(saved),
                                    numberOfLevelIncreased == 0 ?
                                            Mono.just(saved) :
                                            Mono.fromFuture(kafkaTemplate.send(topicNames.getCharacterLevelIncreased().getName(), saved.getId(),
                                                    new CharacterEvent.CharacterLevelIncreasedEvent(character.getId(), character.getLevel().getLevel()))).thenReturn(saved)
                            ).thenReturn(saved))
                            .doOnSuccess(saved -> {
                                logger.debug("added {} exp to character: {}", command.getAmount(), command.getCharacterId());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            });
                });
    }

}
