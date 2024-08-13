package pl.app.comment.application.port;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import pl.app.comment.application.port.in.AddUserVoteUseCase;
import pl.app.comment.application.port.in.CreateVotingUseCase;
import pl.app.comment.application.port.in.RemoveUserVoteUseCase;
import pl.app.comment.application.port.in.command.AddUserVoteCommand;
import pl.app.comment.application.port.in.command.CreateVotingCommand;
import pl.app.comment.application.port.in.command.RemoveUserVoteCommand;
import pl.app.comment.application.port.out.VotingDomainRepository;
import pl.app.config.KafkaTopicConfigurationProperties;
import pl.app.voting.application.domain.UserVote;
import pl.app.voting.application.domain.Voting;
import pl.app.voting.application.domain.VotingEvent;
import pl.app.voting.application.domain.VotingException;
import reactor.core.publisher.Mono;

import java.util.Optional;


@Service
@RequiredArgsConstructor
class VotingService implements
        CreateVotingUseCase,
        AddUserVoteUseCase,
        RemoveUserVoteUseCase {
    private static final Logger logger = LoggerFactory.getLogger(VotingService.class);

    private final VotingDomainRepository votingDomainRepository;
    private final ReactiveMongoTemplate mongoTemplate;
    private final KafkaTemplate<ObjectId, Object> kafkaTemplate;
    private final KafkaTopicConfigurationProperties topicNames;
    @Override
    public Mono<Voting> createVoting(CreateVotingCommand command) {
        logger.debug("creating voting: {}, for domain object: {} of type: {}", command.getIdForNewVoting(), command.getDomainObjectId(), command.getDomainObjectType());
        return Mono.when(verifyThereIsNoDuplicates(command.getDomainObjectId(), command.getDomainObjectType()))
                .doOnError(e -> logger.error("exception occurred while creating voting: {}, exception: {}", command.getIdForNewVoting(), e.getMessage()))
                .then(Mono.defer(() -> {
                    Voting voting = new Voting(command.getDomainObjectId(), command.getDomainObjectType(), command.getIdForNewVoting());
                    var event = new VotingEvent.VotingCreatedEvent(
                            voting.getId(),
                            voting.getDomainObjectId(),
                            voting.getDomainObjectType()
                    );
                    return mongoTemplate.save(voting)
                            .doOnNext(savedVoting -> Mono.fromFuture(kafkaTemplate.send(topicNames.getVotingCreated().getName(), savedVoting.getId(), event)).thenReturn(savedVoting))
                            .doOnSuccess(savedVoting -> {
                                logger.debug("created voting: {}, for domain object: {} of type: {}", voting.getId(), voting.getDomainObjectId(), voting.getDomainObjectType());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            });
                }));
    }


    private Mono<Void> verifyThereIsNoDuplicates(String domainObjectId, String domainObjectType) {
        Query query = Query.query(Criteria
                .where("domainObjectId").is(domainObjectId)
                .and("domainObjectType").is(domainObjectType)
        );
        return mongoTemplate.exists(query, Voting.class)
                .flatMap(exist -> exist ? Mono.error(VotingException.DuplicatedDomainObjectException.fromDomainObject(domainObjectId, domainObjectType)) : Mono.empty());
    }

    @Override
    public Mono<Voting> addUserVote(AddUserVoteCommand command) {
        logger.debug("adding vote to voting: {}, for domain object: {} of type: {}", command.getVotingId(), command.getDomainObjectId(), command.getDomainObjectType());
        return votingDomainRepository.fetchByIdOrDomainObject(command.getVotingId(), command.getDomainObjectId(), command.getDomainObjectType())
                .doOnError(e -> logger.error("exception occurred while adding vote to voting: {}, for domain object: {} of type: {}, exception: {}", command.getVotingId(), command.getDomainObjectId(), command.getDomainObjectType(), e.getMessage()))
                .flatMap(voting -> {
                    voting.addUserVote(command.getUserId(), command.getType());
                    var event = new VotingEvent.VoteAddedEvent(
                            voting.getId(),
                            voting.getDomainObjectId(),
                            voting.getDomainObjectType(),
                            command.getUserId(),
                            command.getType()
                    );
                    return mongoTemplate.save(voting)
                            .doOnNext(savedVoting -> Mono.fromFuture(kafkaTemplate.send(topicNames.getVoteAdded().getName(), voting.getId(), event)).then())
                            .doOnSuccess(unused -> {
                                logger.debug("added vote for voting: {}", voting.getId());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            });
                });
    }


    @Override
    public Mono<Voting> removeUserVote(RemoveUserVoteCommand command) {
        logger.debug("removing vote from voting: {}, for domain object: {} of type: {}", command.getVotingId(), command.getDomainObjectId(), command.getDomainObjectType());
        return votingDomainRepository.fetchByIdOrDomainObject(command.getVotingId(), command.getDomainObjectId(), command.getDomainObjectType())
                .doOnError(e -> logger.error("exception occurred while removing vote from voting: {}, for domain object: {} of type: {}, exception: {}", command.getVotingId(), command.getDomainObjectId(), command.getDomainObjectType(), e.getMessage()))
                .flatMap(voting -> {
                    Optional<UserVote> userVote = voting.removeUserVote(command.getUserId());
                    var event = new VotingEvent.VoteRemovedEvent(
                            voting.getId(),
                            voting.getDomainObjectId(),
                            voting.getDomainObjectType(),
                            command.getUserId(),
                            userVote.map(UserVote::getType).orElse(null)
                    );
                    return mongoTemplate.save(voting)
                            .doOnNext(savedVoting -> Mono.fromFuture(kafkaTemplate.send(topicNames.getVoteRemoved().getName(), voting.getId(), event)))
                            .doOnSuccess(unused -> {
                                logger.debug("removed vote for voting: {}", voting.getVotes());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            });
                });
    }
}
