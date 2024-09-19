package pl.app.god_template.application;

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
import pl.app.god_template.application.domain.GodTemplate;
import pl.app.god_template.application.domain.GodTemplateEvent;
import pl.app.god_template.application.domain.GodTemplateException;
import pl.app.god_template.application.port.in.GodTemplateCommand;
import pl.app.god_template.application.port.in.GodTemplateService;
import pl.app.god_template.application.port.in.GodTemplateDomainRepository;
import reactor.core.publisher.Mono;


@Service
@RequiredArgsConstructor
class GodTemplateServiceImpl implements GodTemplateService {
    private static final Logger logger = LoggerFactory.getLogger(GodTemplateServiceImpl.class);

    private final ReactiveMongoTemplate mongoTemplate;
    private final KafkaTemplate<ObjectId, Object> kafkaTemplate;
    private final KafkaTopicConfigurationProperties topicNames;
    private final GodTemplateDomainRepository godTemplateDomainRepository;

    @Override
    public Mono<GodTemplate> create(GodTemplateCommand.CreateGodTemplateCommand command) {
        logger.debug("creating god template");
        return mongoTemplate.exists(Query.query(Criteria.where("name").is(command.getName())), GodTemplate.class)
                .flatMap(exist -> exist ? Mono.error(GodTemplateException.DuplicatedNameException.fromName(command.getName())) : Mono.empty())
                .doOnError(e -> logger.error("exception occurred while creating god template, exception: {}", e.getMessage()))
                .then(Mono.defer(() -> {
                    GodTemplate domain = new GodTemplate(command.getName(), command.getDescription(), command.getImageId());
                    var event = new GodTemplateEvent.GodTemplateCreatedEvent(
                            domain.getId()
                    );
                    return mongoTemplate.insert(domain)
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getGodTemplateCreated().getName(), saved.getId(), event)).thenReturn(saved))
                            .doOnSuccess(saved -> {
                                logger.debug("created god template {}", saved.getId());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            });
                }));
    }

    @Override
    public Mono<GodTemplate> update(GodTemplateCommand.UpdateGodTemplateCommand command) {
        logger.debug("updating god template {}", command.getId());
        return godTemplateDomainRepository.fetchById(command.getId())
                .doOnError(e -> logger.error("exception occurred while updating god template {} , exception: {}", command.getId(), e.getMessage()))
                .flatMap(domain -> {
                    domain.setName(command.getName());
                    domain.setDescription(command.getDescription());
                    domain.setImageId(command.getImageId());
                    var event = new GodTemplateEvent.GodTemplateUpdatedEvent(
                            domain.getId()
                    );
                    return mongoTemplate.save(domain)
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getGodTemplateUpdated().getName(), saved.getId(), event)).thenReturn(saved))
                            .doOnSuccess(saved -> {
                                logger.debug("updated god template {}", saved.getId());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            });
                });
    }

    @Override
    public Mono<GodTemplate> delete(GodTemplateCommand.DeleteGodTemplateCommand command) {
        logger.debug("deleting god template {}", command.getId());
        return godTemplateDomainRepository.fetchById(command.getId())
                .doOnError(e -> logger.error("exception occurred while deleting god template {} , exception: {}", command.getId(), e.getMessage()))
                .flatMap(domain -> {
                    var event = new GodTemplateEvent.GodTemplateDeletedEvent(
                            domain.getId()
                    );
                    return mongoTemplate.remove(domain)
                            .flatMap(unused -> Mono.fromFuture(kafkaTemplate.send(topicNames.getGodTemplateUpdated().getName(), domain.getId(), event)).thenReturn(domain))
                            .doOnSuccess(saved -> {
                                logger.debug("updated god template {}", domain.getId());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            });
                });
    }
}
