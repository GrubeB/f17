package pl.app.comment.application.port;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import pl.app.voting.application.domain.VotingEvent;
import pl.app.comment.application.port.in.AddUserVoteRequestUseCase;
import pl.app.comment.application.port.in.CreateVotingRequestUseCase;
import pl.app.comment.application.port.in.RemoveUserVoteRequestUseCase;
import pl.app.comment.application.port.in.command.AddUserVoteRequestCommand;
import pl.app.comment.application.port.in.command.CreateVotingRequestCommand;
import pl.app.comment.application.port.in.command.RemoveUserVoteRequestCommand;

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
    public ObjectId createVotingRequest(CreateVotingRequestCommand command) {
        final ObjectId idForNewVoting = ObjectId.get();
        final VotingEvent.CreateVotingRequestedEvent event = new VotingEvent.CreateVotingRequestedEvent(
                idForNewVoting,
                command.getDomainObjectId(),
                command.getDomainObjectType()
        );
        kafkaTemplate.send(createVotingRequestedTopicName, idForNewVoting, event);
        logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
        return idForNewVoting;
    }

    @Override
    public void addUserVoteRequest(AddUserVoteRequestCommand command) {
        var event = new VotingEvent.AddVoteRequestedEvent(
                command.getVotingId(),
                command.getDomainObjectId(),
                command.getDomainObjectType(),
                command.getUserId(),
                command.getType()
        );
        kafkaTemplate.send(addVoteRequestedTopicName, command.getVotingId(), event);
        logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
    }


    @Override
    public void removeUserVote(RemoveUserVoteRequestCommand command) {
        var event = new VotingEvent.RemoveVoteRequestedEvent(
                command.getVotingId(),
                command.getDomainObjectId(),
                command.getDomainObjectType(),
                command.getUserId()
        );
        kafkaTemplate.send(removeVoteRequestedTopicName, command.getVotingId(), event);
        logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
    }
}
