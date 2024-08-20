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
import pl.app.character.application.domain.Character;
import pl.app.character.application.domain.CharacterEvent;
import pl.app.character.application.domain.CharacterException;
import pl.app.character.application.port.in.CharacterCommand;
import pl.app.character.application.port.in.CharacterMoneyService;
import pl.app.character.application.port.out.CharacterDomainRepository;
import pl.app.config.KafkaTopicConfigurationProperties;
import reactor.core.publisher.Mono;


@Service
@RequiredArgsConstructor
class CharacterMoneyServiceImpl implements CharacterMoneyService {
    private static final Logger logger = LoggerFactory.getLogger(CharacterMoneyServiceImpl.class);

    private final CharacterDomainRepository domainRepository;
    private final ReactiveMongoTemplate mongoTemplate;
    private final KafkaTemplate<ObjectId, Object> kafkaTemplate;
    private final KafkaTopicConfigurationProperties topicNames;

    @Override
    public Mono<Character> addMoney(CharacterCommand.AddMoneyCommand command) {
        logger.debug("adding {} money to character: {}", command.getAmount(), command.getCharacterId());
        return domainRepository.fetchById(command.getCharacterId())
                .doOnError(e -> logger.error("exception occurred while adding {} money to character: {}, exception: {}", command.getAmount(), command.getCharacterId(), e.getMessage()))
                .flatMap(character -> {
                    character.getMoney().addMoney(command.getAmount());
                    var event = new CharacterEvent.MoneyAddedEvent(
                            character.getId(),
                            command.getAmount()
                    );
                    return mongoTemplate.save(character)
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getMoneyAdded().getName(), saved.getId(), event)).thenReturn(saved))
                            .doOnSuccess(saved -> {
                                logger.debug("added {} money to character: {}", command.getAmount(), command.getCharacterId());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            });
                });
    }

    @Override
    public Mono<Character> subtractMoney(CharacterCommand.SubtractMoneyCommand command) {
        logger.debug("subtracting {} money from character: {}", command.getAmount(), command.getCharacterId());
        return domainRepository.fetchById(command.getCharacterId())
                .doOnError(e -> logger.error("exception occurred while subtracting {} money from character: {}, exception: {}", command.getAmount(), command.getCharacterId(), e.getMessage()))
                .flatMap(character -> {
                    character.getMoney().subtractMoney(command.getAmount());
                    var event = new CharacterEvent.MoneySubtractedEvent(
                            character.getId(),
                            command.getAmount()
                    );
                    return mongoTemplate.save(character)
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getMoneySubtracted().getName(), saved.getId(), event)).thenReturn(saved))
                            .doOnSuccess(saved -> {
                                logger.debug("subtracted {} money from character: {}", command.getAmount(), command.getCharacterId());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            });
                });
    }
}
