package pl.app.character_template.application;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import pl.app.character_template.application.domain.CharacterTemplate;
import pl.app.character_template.application.domain.CharacterTemplateEvent;
import pl.app.character_template.application.domain.CharacterTemplateException;
import pl.app.character_template.application.port.in.CharacterTemplateService;
import pl.app.character_template.application.port.out.CharacterTemplateDomainRepository;
import pl.app.character_template.in.CharacterCommand;
import pl.app.config.KafkaTopicConfigurationProperties;
import reactor.core.publisher.Mono;


@Service
@RequiredArgsConstructor
class CharacterTemplateServiceImpl implements CharacterTemplateService {
    private static final Logger logger = LoggerFactory.getLogger(CharacterTemplateServiceImpl.class);

    private final ReactiveMongoTemplate mongoTemplate;
    private final KafkaTemplate<ObjectId, Object> kafkaTemplate;
    private final KafkaTopicConfigurationProperties topicNames;
    private final CharacterTemplateDomainRepository characterTemplateDomainRepository;


    @Override
    public Mono<CharacterTemplate> create(CharacterCommand.CreateCharacterTemplateCommand command) {
        logger.debug("creating character template");
        return mongoTemplate.exists(Query.query(Criteria.where("name").is(command.getName())), CharacterTemplate.class)
                .flatMap(exist -> exist ? Mono.error(CharacterTemplateException.DuplicatedNameException.fromName(command.getName())) : Mono.empty())
                .doOnError(e -> logger.error("exception occurred while creating character template, exception: {}", e.getMessage()))
                .then(Mono.defer(() -> {
                    var domain = new CharacterTemplate(command.getName(), command.getDescription(), command.getRace(), command.getProfession(), command.getImageId(),
                            command.getBaseStatistics(), command.getPerLevelStatistics());
                    var event = new CharacterTemplateEvent.CharacterTemplateCreatedEvent(
                            domain.getId()
                    );
                    return mongoTemplate.insert(domain)
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getCharacterTemplateCreated().getName(), saved.getId(), event)).thenReturn(saved))
                            .doOnSuccess(saved -> {
                                logger.debug("created character template {}", saved.getId());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            });
                }));
    }

    @Override
    public Mono<CharacterTemplate> update(CharacterCommand.UpdateCharacterTemplateCommand command) {
        logger.debug("updating character template {}", command.getId());
        return characterTemplateDomainRepository.fetchById(command.getId())
                .doOnError(e -> logger.error("exception occurred while updating character template {} , exception: {}", command.getId(), e.getMessage()))
                .flatMap(domain -> {
                    domain.setName(command.getName());
                    domain.setDescription(command.getDescription());
                    domain.setRace(command.getRace());
                    domain.setProfession(command.getProfession());
                    domain.setImageId(command.getImageId());
                    domain.setBaseStatistics(command.getBaseStatistics());
                    domain.setPerLevelStatistics(command.getPerLevelStatistics());
                    var event = new CharacterTemplateEvent.CharacterTemplateUpdatedEvent(
                            domain.getId()
                    );
                    return mongoTemplate.save(domain)
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getCharacterTemplateUpdated().getName(), saved.getId(), event)).thenReturn(saved))
                            .doOnSuccess(saved -> {
                                logger.debug("updated character template {}", saved.getId());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            });
                });
    }

    @Override
    public Mono<CharacterTemplate> delete(CharacterCommand.DeleteCharacterTemplateCommand command) {
        logger.debug("deleting character template {}", command.getId());
        return characterTemplateDomainRepository.fetchById(command.getId())
                .doOnError(e -> logger.error("exception occurred while deleting character template {} , exception: {}", command.getId(), e.getMessage()))
                .flatMap(domain -> {
                    var event = new CharacterTemplateEvent.CharacterTemplateDeletedEvent(
                            domain.getId()
                    );
                    return mongoTemplate.remove(domain)
                            .flatMap(unused -> Mono.fromFuture(kafkaTemplate.send(topicNames.getCharacterTemplateDeleted().getName(), domain.getId(), event)).thenReturn(domain))
                            .doOnSuccess(saved -> {
                                logger.debug("deleted character template {}", domain.getId());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            });
                });
    }
}
