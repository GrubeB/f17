package pl.app.comment.adapter.in;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import pl.app.comment.application.port.in.AddUserVoteUseCase;
import pl.app.comment.application.port.in.CreateVotingUseCase;
import pl.app.comment.application.port.in.RemoveUserVoteUseCase;
import pl.app.comment.application.port.in.VotingCommand.AddUserVoteCommand;
import pl.app.comment.application.port.in.VotingCommand.CreateVotingCommand;
import pl.app.comment.application.port.in.VotingCommand.RemoveUserVoteCommand;
import pl.app.voting.application.domain.VotingEvent;
import pl.app.voting.application.domain.VotingException;

@Component
@RequiredArgsConstructor
class VotingRequestedEventListener {
    private final Logger logger = LoggerFactory.getLogger(VotingRequestedEventListener.class);
    private final CreateVotingUseCase createVotingUseCase;
    private final AddUserVoteUseCase addUserVoteUseCase;
    private final RemoveUserVoteUseCase removeUserVoteUseCase;

    @KafkaListener(
            id = "create-voting-requested-event-listener",
            groupId = "${app.kafka.consumer.group-id}",
            topics = "${app.kafka.topic.create-voting-requested.name}"
    )
    public void createVoting(ConsumerRecord<ObjectId, VotingEvent.CreateVotingRequestedEvent> record) {
        logger.debug("received event {} {}-{} key: {},value: {}", record.value().getClass().getSimpleName(), record.partition(), record.offset(), record.key(), record.value());
        final var event = record.value();
        try {
            var command = new CreateVotingCommand(event.getVotingId(), event.getDomainObjectId(), event.getDomainObjectType());
            createVotingUseCase.createVoting(command).block();
        } catch (VotingException.DuplicatedDomainObjectException exception) {
            logger.debug("exception occurred while processing event {} {}-{} key: {}, value: {}, exception: {}", record.value().getClass().getSimpleName(),
                    record.partition(), record.offset(), record.key(), record.value(), exception.getMessage());
        }
    }

    @KafkaListener(
            id = "add-vote-requested-event-listener",
            groupId = "${app.kafka.consumer.group-id}",
            topics = "${app.kafka.topic.add-vote-requested.name}"
    )
    public void addUserVote(ConsumerRecord<ObjectId, VotingEvent.AddVoteRequestedEvent> record) {
        logger.debug("received event {} {}-{} key: {},value: {}", record.value().getClass().getSimpleName(), record.partition(), record.offset(), record.key(), record.value());
        final var event = record.value();
        var command = new AddUserVoteCommand(
                event.getVotingId(),
                event.getDomainObjectId(),
                event.getDomainObjectType(),
                event.getUserId(),
                event.getType()
        );
        addUserVoteUseCase.addUserVote(command).block();
    }

    @KafkaListener(
            id = "remove-vote-requested-event-listener",
            groupId = "${app.kafka.consumer.group-id}",
            topics = "${app.kafka.topic.remove-vote-requested.name}"
    )
    public void removeUserVote(ConsumerRecord<ObjectId, VotingEvent.RemoveVoteRequestedEvent> record) {
        logger.debug("received event {} {}-{} key: {},value: {}", record.value().getClass().getSimpleName(), record.partition(), record.offset(), record.key(), record.value());
        final var event = record.value();
        var command = new RemoveUserVoteCommand(
                event.getVotingId(),
                event.getDomainObjectId(),
                event.getDomainObjectType(),
                event.getUserId()
        );
        removeUserVoteUseCase.removeUserVote(command).block();
    }
}
