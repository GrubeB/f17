package pl.app.voting.application.port;

import org.assertj.core.api.Assertions;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import pl.app.AbstractIntegrationTest;
import pl.app.voting.application.port.VotingService;
import pl.app.voting.application.port.in.VotingCommand.AddUserVoteCommand;
import pl.app.voting.application.port.in.VotingCommand.CreateVotingCommand;
import pl.app.voting.application.port.in.VotingCommand.RemoveUserVoteCommand;
import pl.app.voting.application.port.out.VotingDomainRepository;
import pl.app.voting.application.domain.UserVote;
import pl.app.voting.application.domain.Voting;
import pl.app.voting.application.domain.VotingEvent;
import pl.app.voting.application.domain.VotingException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class VotingServiceTest extends AbstractIntegrationTest {
    @Autowired
    private VotingService votingService;
    @SpyBean
    private VotingDomainRepository votingDomainRepository;
    @SpyBean
    private ReactiveMongoTemplate mongoTemplate;
    @SpyBean
    private KafkaTemplate<ObjectId, Object> kafkaTemplate;

    @Test
    void contextLoads() {
        Assertions.assertThat(votingDomainRepository).isNotNull();
        Assertions.assertThat(mongoTemplate).isNotNull();
        Assertions.assertThat(kafkaTemplate).isNotNull();
        Assertions.assertThat(votingService).isNotNull();
    }

    @Test
    void createVoting_shouldCreateVoting() {
        final var votingId = ObjectId.get();
        final var domainObjectId = UUID.randomUUID().toString();
        final var domainObjectType = "TEST_TYPE";

        Mono<Voting> createVoting = votingService.createVoting(new CreateVotingCommand(votingId, domainObjectId, domainObjectType));

        StepVerifier.create(createVoting)
                .expectNextMatches(actual -> actual.getId().equals(votingId))
                .verifyComplete();
        Mockito.verify(mongoTemplate, Mockito.times(1)).save(ArgumentMatchers.any(Voting.class));
        Mockito.verify(kafkaTemplate, Mockito.times(1)).send(ArgumentMatchers.anyString(), ArgumentMatchers.any(ObjectId.class), ArgumentMatchers.any(VotingEvent.VotingCreatedEvent.class));
    }

    @Test
    void createVoting_shouldThrowExceptionWhenVotingIsDuplicated() {
        final var votingId = ObjectId.get();
        final var domainObjectId = UUID.randomUUID().toString();
        final var domainObjectType = "TEST_TYPE";

        Mono<Voting> createVoting = votingService.createVoting(new CreateVotingCommand(votingId, domainObjectId, domainObjectType));
        createVoting.block();

        StepVerifier.create(createVoting)
                .expectError(VotingException.DuplicatedDomainObjectException.class);
    }

    @Test
    void addUserVote_shouldAddUserVote() {
        final var votingId = ObjectId.get();
        final var domainObjectId = UUID.randomUUID().toString();
        final var domainObjectType = "TEST_TYPE";
        final var userId = UUID.randomUUID().toString();

        votingService.createVoting(new CreateVotingCommand(votingId, domainObjectId, domainObjectType)).block();
        Mono<Voting> addUserVote = votingService.addUserVote(new AddUserVoteCommand(votingId, domainObjectId, domainObjectType, userId, "LIKE"));

        StepVerifier.create(addUserVote)
                .expectNextMatches(actual -> {
                    if (actual.getUserVoteByUserId(userId).isEmpty()) {
                        return false;
                    }
                    UserVote userVote = actual.getUserVoteByUserId(userId).get();
                    return userVote.getUserId().equals(userId) && userVote.getType().equals("LIKE");
                })
                .verifyComplete();
        Mockito.verify(mongoTemplate, Mockito.times(2)).save(ArgumentMatchers.any(Voting.class));
        Mockito.verify(kafkaTemplate, Mockito.times(1)).send(ArgumentMatchers.anyString(), ArgumentMatchers.any(ObjectId.class), ArgumentMatchers.any(VotingEvent.VoteAddedEvent.class));
    }

    @Test
    void addUserVote_shouldThrowExceptionWhenVotingDoesNotExist() {
        final var votingId = ObjectId.get();
        final var domainObjectId = UUID.randomUUID().toString();
        final var domainObjectType = "TEST_TYPE";
        final var userId = UUID.randomUUID().toString();

        Mono<Voting> addUserVote = votingService.addUserVote(new AddUserVoteCommand(votingId, domainObjectId, domainObjectType, userId, "LIKE"));

        StepVerifier.create(addUserVote)
                .expectError(VotingException.NotFoundVotingException.class);
    }

    @Test
    void removeUserVote_shouldRemoveUserVote() {
        final var votingId = ObjectId.get();
        final var domainObjectId = UUID.randomUUID().toString();
        final var domainObjectType = "TEST_TYPE";
        final var userId = UUID.randomUUID().toString();

        votingService.createVoting(new CreateVotingCommand(votingId, domainObjectId, domainObjectType)).block();
        votingService.addUserVote(new AddUserVoteCommand(votingId, domainObjectId, domainObjectType, userId, "LIKE")).block();
        Mono<Voting> removeUserVote = votingService.removeUserVote(new RemoveUserVoteCommand(votingId, domainObjectId, domainObjectType, userId));

        StepVerifier.create(removeUserVote)
                .expectNextMatches(actual -> actual.getUserVoteByUserId(userId).isEmpty())
                .verifyComplete();
        Mockito.verify(mongoTemplate, Mockito.times(3)).save(ArgumentMatchers.any(Voting.class));
        Mockito.verify(kafkaTemplate, Mockito.times(1)).send(ArgumentMatchers.anyString(), ArgumentMatchers.any(ObjectId.class), ArgumentMatchers.any(VotingEvent.VoteRemovedEvent.class));
    }

    @Test
    void removeUserVote_shouldThrowExceptionWhenVotingDoesNotExist() {
        final var votingId = ObjectId.get();
        final var domainObjectId = UUID.randomUUID().toString();
        final var domainObjectType = "TEST_TYPE";
        final var userId = UUID.randomUUID().toString();

        Mono<Voting> removeUserVote = votingService.removeUserVote(new RemoveUserVoteCommand(votingId, domainObjectId, domainObjectType, userId));

        StepVerifier.create(removeUserVote)
                .expectError(VotingException.NotFoundVotingException.class);
    }
}