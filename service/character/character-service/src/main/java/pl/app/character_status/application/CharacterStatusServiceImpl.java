package pl.app.character_status.application;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import pl.app.character_status.application.domain.CharacterStatus;
import pl.app.character_status.application.domain.CharacterStatusEvent;
import pl.app.character_status.application.port.in.CharacterStatusCommand;
import pl.app.character_status.application.port.in.CharacterStatusDomainRepository;
import pl.app.character_status.application.port.in.CharacterStatusService;
import pl.app.config.KafkaTopicConfigurationProperties;
import reactor.core.publisher.Mono;


@Service
@RequiredArgsConstructor
class CharacterStatusServiceImpl implements CharacterStatusService {
    private static final Logger logger = LoggerFactory.getLogger(CharacterStatusServiceImpl.class);

    private final CharacterStatusDomainRepository domainRepository;
    private final ReactiveMongoTemplate mongoTemplate;
    private final KafkaTemplate<ObjectId, Object> kafkaTemplate;
    private final KafkaTopicConfigurationProperties topicNames;

    @Override
    public Mono<CharacterStatus> changeStatus(CharacterStatusCommand.ChangeCharacterStatusCommand command) {
        logger.debug("changing character: {} status to: {}", command.getCharacterId(), command.getType());
        return domainRepository.fetchByCharacterId(command.getCharacterId())
                .doOnError(e -> logger.error("exception occurred while changing character: {} status to: {}, exception: {}", command.getCharacterId(), command.getType(), e.getMessage()))
                .switchIfEmpty(Mono.just(new CharacterStatus(command.getCharacterId())))
                .flatMap(domain -> {
                    domain.setType(command.getType());
                    var event = new CharacterStatusEvent.CharacterStatusChangedEvent(
                            domain.getCharacterId(),
                            domain.getType()
                    );
                    return mongoTemplate.insert(domain)
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getCharacterStatusChanged().getName(), saved.getCharacterId(), event)).thenReturn(saved))
                            .doOnSuccess(saved -> {
                                logger.debug("changed character:{} status", saved.getCharacterId());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            });
                });
    }
}
