package pl.app.comment.application.port;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import pl.app.comment.application.port.in.AddUserVoteRequestUseCase;
import pl.app.comment.application.port.in.CreateVotingRequestUseCase;
import pl.app.comment.application.port.in.RemoveUserVoteRequestUseCase;
import pl.app.comment.application.port.in.command.AddUserVoteRequestCommand;
import pl.app.comment.application.port.in.command.CreateVotingRequestCommand;
import pl.app.comment.application.port.in.command.RemoveUserVoteRequestCommand;
import pl.app.voting.application.domain.VotingEvent;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
class VotingRequestService implements
        CreateVotingRequestUseCase,
        AddUserVoteRequestUseCase,
        RemoveUserVoteRequestUseCase {
    private final Logger logger = LoggerFactory.getLogger(VotingService.class);
    private final KafkaTemplate<ObjectId, Object> kafkaTemplate;
    @Value("${app.kafka.topic.create-voting-requested.name}")
    private String createVotingRequestedTopicName;
    @Value("${app.kafka.topic.add-vote-requested.name}")
    private String addVoteRequestedTopicName;
    @Value("${app.kafka.topic.remove-vote-requested.name}")
    private String removeVoteRequestedTopicName;

    @Override
    public Mono<ObjectId> createVotingRequest(CreateVotingRequestCommand command) {
        final ObjectId idForNewVoting = ObjectId.get();
        final VotingEvent.CreateVotingRequestedEvent event = new VotingEvent.CreateVotingRequestedEvent(
                idForNewVoting,
                command.getDomainObjectId(),
                command.getDomainObjectType()
        );
        return Mono.fromFuture(kafkaTemplate.send(createVotingRequestedTopicName, idForNewVoting, event))
                .doOnSuccess(result -> logger.debug("send {} - {}", event.getClass().getSimpleName(), event))
                .thenReturn(idForNewVoting);
    }

    @Override
    public Mono<Void> addUserVoteRequest(AddUserVoteRequestCommand command) {
        var event = new VotingEvent.AddVoteRequestedEvent(
                command.getVotingId(),
                command.getDomainObjectId(),
                command.getDomainObjectType(),
                command.getUserId(),
                command.getType()
        );
        return Mono.fromFuture(kafkaTemplate.send(addVoteRequestedTopicName, command.getVotingId(), event))
                .doOnSuccess(unused -> logger.debug("send {} - {}", event.getClass().getSimpleName(), event))
                .then();
    }


    @Override
    public Mono<Void> removeUserVote(RemoveUserVoteRequestCommand command) {
        var event = new VotingEvent.RemoveVoteRequestedEvent(
                command.getVotingId(),
                command.getDomainObjectId(),
                command.getDomainObjectType(),
                command.getUserId()
        );
        return Mono.fromFuture(kafkaTemplate.send(removeVoteRequestedTopicName, command.getVotingId(), event))
                .doOnSuccess(unused -> logger.debug("send {} - {}", event.getClass().getSimpleName(), event))
                .then();
    }
}
