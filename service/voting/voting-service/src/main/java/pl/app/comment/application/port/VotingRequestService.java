package pl.app.comment.application.port;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import pl.app.comment.application.port.in.AddUserVoteRequestUseCase;
import pl.app.comment.application.port.in.CreateVotingRequestUseCase;
import pl.app.comment.application.port.in.RemoveUserVoteRequestUseCase;
import pl.app.comment.application.port.in.VotingCommand.AddUserVoteRequestCommand;
import pl.app.comment.application.port.in.VotingCommand.CreateVotingRequestCommand;
import pl.app.comment.application.port.in.VotingCommand.RemoveUserVoteRequestCommand;
import pl.app.config.KafkaTopicConfigurationProperties;
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
    private final KafkaTopicConfigurationProperties topicNames;

    @Override
    public Mono<ObjectId> createVotingRequest(CreateVotingRequestCommand command) {
        final ObjectId idForNewVoting = ObjectId.get();
        final VotingEvent.CreateVotingRequestedEvent event = new VotingEvent.CreateVotingRequestedEvent(
                idForNewVoting,
                command.getDomainObjectId(),
                command.getDomainObjectType()
        );
        return Mono.fromFuture(kafkaTemplate.send(topicNames.getCreateVotingRequested().getName(), idForNewVoting, event))
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
        return Mono.fromFuture(kafkaTemplate.send(topicNames.getAddVoteRequested().getName(), command.getVotingId(), event))
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
        return Mono.fromFuture(kafkaTemplate.send(topicNames.getRemoveVoteRequested().getName(), command.getVotingId(), event))
                .doOnSuccess(unused -> logger.debug("send {} - {}", event.getClass().getSimpleName(), event))
                .then();
    }
}
