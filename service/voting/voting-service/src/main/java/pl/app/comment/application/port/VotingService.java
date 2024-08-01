package pl.app.comment.application.port;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import pl.app.comment.application.domain.UserVote;
import pl.app.comment.application.domain.Voting;
import pl.app.comment.application.domain.VotingEvent;
import pl.app.comment.application.domain.VotingException;
import pl.app.comment.application.port.in.AddUserVoteUseCase;
import pl.app.comment.application.port.in.CreateVotingUseCase;
import pl.app.comment.application.port.in.RemoveUserVoteUseCase;
import pl.app.comment.application.port.in.command.AddUserVoteCommand;
import pl.app.comment.application.port.in.command.CreateVotingCommand;
import pl.app.comment.application.port.in.command.RemoveUserVoteCommand;
import pl.app.comment.application.port.out.VotingDomainRepository;

import java.util.Optional;


@Service
@RequiredArgsConstructor
class VotingService implements
        CreateVotingUseCase,
        AddUserVoteUseCase,
        RemoveUserVoteUseCase {
    private final Logger logger = LoggerFactory.getLogger(VotingService.class);

    private final VotingDomainRepository votingDomainRepository;
    private final MongoTemplate mongoTemplate;
    private final KafkaTemplate<ObjectId, Object> kafkaTemplate;
    @Value("${app.kafka.topic.voting-created.name}")
    private String votingCreatedTopicName;
    @Value("${app.kafka.topic.vote-added.name}")
    private String voteAddedTopicName;
    @Value("${app.kafka.topic.vote-removed.name}")
    private String voteRemovedTopicName;

    @Override
    public Voting createVoting(CreateVotingCommand command) {
        verifyThereIsNoDuplicates(command.getDomainObjectId(), command.getDomainObjectType());
        Voting voting = new Voting(command.getDomainObjectId(), command.getDomainObjectType(), command.getIdForNewVoting());
        mongoTemplate.save(voting);
        var event = new VotingEvent.VotingCreatedEvent(
                voting.getId(),
                voting.getDomainObjectType(),
                voting.getDomainObjectId()
        );
        kafkaTemplate.send(votingCreatedTopicName, voting.getId(), event);
        logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
        logger.debug("created voting with id: {}, for domain object: {} of type: {}", voting.getId(),
                voting.getDomainObjectId(), voting.getDomainObjectType());
        return voting;
    }

    private void verifyThereIsNoDuplicates(String domainObjectId, String domainObjectType) {
        Query query = Query.query(Criteria
                .where("domainObjectId").is(domainObjectId)
                .and("domainObjectType").is(domainObjectType)
        );
        if (mongoTemplate.query(Voting.class).matching(query).one().isPresent()) {
            throw VotingException.DuplicatedDomainObjectException.fromDomainObject(domainObjectId, domainObjectType);
        }
    }

    @Override
    public void addUserVote(AddUserVoteCommand command) {
        Voting voting = votingDomainRepository.fetchByIdOrDomainObject(command.getVotingId(), command.getDomainObjectId(), command.getDomainObjectType());
        voting.addUserVote(command.getUserId(), command.getType());
        mongoTemplate.save(voting);
        var event = new VotingEvent.VoteAddedEvent(
                voting.getId(),
                voting.getDomainObjectType(),
                voting.getDomainObjectId(),
                command.getUserId(),
                command.getType()
        );
        kafkaTemplate.send(voteAddedTopicName, voting.getId(), event);
        logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
        logger.debug("added user vote for voting with id: {}", voting.getVotes());
    }


    @Override
    public void removeUserVote(RemoveUserVoteCommand command) {
        Voting voting = votingDomainRepository.fetchByIdOrDomainObject(command.getVotingId(), command.getDomainObjectId(), command.getDomainObjectType());
        Optional<UserVote> userVote = voting.removeUserVote(command.getUserId());
        mongoTemplate.save(voting);
        var event = new VotingEvent.VoteRemovedEvent(
                voting.getId(),
                voting.getDomainObjectType(),
                voting.getDomainObjectId(),
                command.getUserId(),
                userVote.map(UserVote::getType).orElse(null)
        );
        kafkaTemplate.send(voteRemovedTopicName, voting.getId(), event);
        logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
        logger.debug("removed user vote for voting with id: {}", voting.getVotes());
    }
}
