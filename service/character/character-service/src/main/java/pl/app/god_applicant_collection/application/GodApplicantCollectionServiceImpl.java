package pl.app.god_applicant_collection.application;

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
import pl.app.god_applicant_collection.application.domain.GodApplicant;
import pl.app.god_applicant_collection.application.domain.GodApplicantCollection;
import pl.app.god_applicant_collection.application.domain.GodApplicantCollectionEvent;
import pl.app.god_applicant_collection.application.domain.GodApplicantCollectionException;
import pl.app.god_applicant_collection.application.port.in.GodApplicantCollectionCommand;
import pl.app.god_applicant_collection.application.port.in.GodApplicantCollectionService;
import pl.app.god_applicant_collection.application.port.out.CharacterDomainRepository;
import pl.app.god_applicant_collection.application.port.out.GodApplicantCollectionDomainRepository;
import reactor.core.publisher.Mono;


@Service
@RequiredArgsConstructor
class GodApplicantCollectionServiceImpl implements GodApplicantCollectionService {
    private static final Logger logger = LoggerFactory.getLogger(GodApplicantCollectionServiceImpl.class);

    private final ReactiveMongoTemplate mongoTemplate;
    private final KafkaTemplate<ObjectId, Object> kafkaTemplate;
    private final KafkaTopicConfigurationProperties topicNames;
    private final CharacterDomainRepository characterDomainRepository;
    private final GodApplicantCollectionDomainRepository godApplicantCollectionDomainRepository;

    @Override
    public Mono<GodApplicantCollection> createGodApplicantCollection(GodApplicantCollectionCommand.CreateGofApplicantCollectionCommand command) {
        logger.debug("creating god applicant collection for god: {}", command.getGodId());
        return mongoTemplate.exists(Query.query(Criteria.where("godId").is(command.getGodId())), GodApplicantCollection.class)
                .flatMap(exist -> exist ? Mono.error(GodApplicantCollectionException.DuplicatedGodsException.fromGodId(command.getGodId().toHexString())) : Mono.empty())
                .doOnError(e -> logger.error("exception occurred while creating god applicant collection for god: {}, exception: {}", command.getGodId(), e.getMessage()))
                .then(Mono.defer(() -> {
                    GodApplicantCollection domain = new GodApplicantCollection(command.getGodId());
                    var event = new GodApplicantCollectionEvent.GodApplicantCollectionCreatedEvent(
                            domain.getId(),
                            domain.getGodId()
                    );
                    return mongoTemplate.insert(domain)
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getGodApplicationCollectionCreated().getName(), saved.getId(), event)).thenReturn(saved))
                            .doOnSuccess(saved -> {
                                logger.debug("created god applicant collection {}, for god: {}", saved.getId(), saved.getGodId());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            });
                }));
    }

    @Override
    public Mono<GodApplicantCollection> create(GodApplicantCollectionCommand.CreateGodApplicantCommand command) {
        logger.debug("creating god applicant {} for god: {}", command.getCharacterId(), command.getGodId());
        return mongoTemplate.exists(Query.query(Criteria.where("characterId").is(command.getCharacterId())), GodApplicant.class)
                .flatMap(exist -> exist ? Mono.error(GodApplicantCollectionException.DuplicatedCharacterException.fromCharacterId(command.getCharacterId().toHexString())) : Mono.empty())
                .doOnError(e -> logger.error("exception occurred while creating god applicant {} for god: {}, exception: {}", command.getCharacterId(), command.getGodId(), e.getMessage()))
                .then(godApplicantCollectionDomainRepository.fetchByGodId(command.getGodId()))
                .flatMap(domain -> {
                    GodApplicant applicant = domain.addApplicant(new GodApplicant(command.getCharacterId()));
                    var event = new GodApplicantCollectionEvent.GodApplicantCreatedEvent(
                            domain.getId(),
                            domain.getGodId(),
                            applicant.getId(),
                            applicant.getCharacterId()
                    );
                    return mongoTemplate.insert(applicant)
                            .flatMap(unused -> mongoTemplate.save(domain))
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getGodApplicationCreated().getName(), saved.getId(), event)).thenReturn(saved))
                            .doOnSuccess(saved -> {
                                logger.debug("created god applicant {} for god: {}", applicant.getCharacterId(), saved.getGodId());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            });
                });
    }

    @Override
    public Mono<GodApplicantCollection> remove(GodApplicantCollectionCommand.RemoveGodApplicantCommand command) {
        logger.debug("removing god applicant {} for god: {}", command.getCharacterId(), command.getGodId());
        return godApplicantCollectionDomainRepository.fetchByGodId(command.getGodId())
                .doOnError(e -> logger.error("exception occurred while removing god applicant {} for god: {}, exception: {}", command.getCharacterId(), command.getGodId(), e.getMessage()))
                .flatMap(domain -> {
                    GodApplicant applicant = domain.removeApplicantByCharacterId(command.getCharacterId());
                    var event = new GodApplicantCollectionEvent.GodApplicantRemovedEvent(
                            domain.getId(),
                            domain.getGodId(),
                            applicant.getId(),
                            applicant.getCharacterId()
                    );
                    return mongoTemplate.remove(applicant)
                            .flatMap(unused -> mongoTemplate.save(domain))
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getGodApplicationRemoved().getName(), saved.getId(), event)).thenReturn(saved))
                            .doOnSuccess(saved -> {
                                logger.debug("removed god applicant {}, for god: {}", applicant.getCharacterId(), saved.getGodId());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            });
                });
    }

    @Override
    public Mono<GodApplicantCollection> accept(GodApplicantCollectionCommand.AcceptGodApplicantCommand command) {
        logger.debug("accepting god applicant {} for god: {}", command.getCharacterId(), command.getGodId());
        return godApplicantCollectionDomainRepository.fetchByGodId(command.getGodId())
                .doOnError(e -> logger.error("exception occurred while accepting god applicant {} for god: {}, exception: {}", command.getCharacterId(), command.getGodId(), e.getMessage()))
                .flatMap(domain -> {
                    GodApplicant applicant = domain.acceptApplicantByCharacterId(command.getCharacterId());
                    var event = new GodApplicantCollectionEvent.GodApplicantAcceptedEvent(
                            domain.getId(),
                            domain.getGodId(),
                            applicant.getId(),
                            applicant.getCharacterId()
                    );
                    return mongoTemplate.remove(applicant)
                            .flatMap(unused -> mongoTemplate.save(domain))
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getGodApplicationAccepted().getName(), saved.getId(), event)).thenReturn(saved))
                            .doOnSuccess(saved -> {
                                logger.debug("accepted god applicant {}, for god: {}", applicant.getCharacterId(), saved.getGodId());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            });
                });
    }

    @Override
    public Mono<GodApplicantCollection> reject(GodApplicantCollectionCommand.RejectGodApplicantCommand command) {
        logger.debug("rejecting god applicant {} for god: {}", command.getCharacterId(), command.getGodId());
        return godApplicantCollectionDomainRepository.fetchByGodId(command.getGodId())
                .doOnError(e -> logger.error("exception occurred while rejecting god applicant {} for god: {}, exception: {}", command.getCharacterId(), command.getGodId(), e.getMessage()))
                .flatMap(domain -> {
                    GodApplicant applicant = domain.rejectApplicantByCharacterId(command.getCharacterId());
                    var event = new GodApplicantCollectionEvent.GodApplicantRejectedEvent(
                            domain.getId(),
                            domain.getGodId(),
                            applicant.getId(),
                            applicant.getCharacterId()
                    );
                    return mongoTemplate.remove(applicant)
                            .flatMap(unused -> mongoTemplate.save(domain))
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getGodApplicationAccepted().getName(), saved.getId(), event)).thenReturn(saved))
                            .doOnSuccess(saved -> {
                                logger.debug("rejected god applicant {}, for god: {}", applicant.getCharacterId(), saved.getGodId());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            });
                });
    }
}
