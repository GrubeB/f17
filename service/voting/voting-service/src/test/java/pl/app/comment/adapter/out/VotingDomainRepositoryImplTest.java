package pl.app.comment.adapter.out;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import pl.app.AbstractIntegrationTest;
import pl.app.voting.application.domain.Voting;
import pl.app.voting.application.domain.VotingException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

@DataMongoTest
@ComponentScan(basePackageClasses = VotingDomainRepositoryImpl.class)
class VotingDomainRepositoryImplTest extends AbstractIntegrationTest {

    @Autowired
    private VotingDomainRepositoryImpl votingDomainRepository;
    @Autowired
    private ReactiveMongoTemplate mongoTemplate;

    @Test
    void fetchById_shouldReturnOne_whenExists() {
        final var votingId = ObjectId.get();
        final var domainObjectId = UUID.randomUUID().toString();
        final var domainObjectType = "TEST_TYPE";

        Voting voting = new Voting(domainObjectId, domainObjectType, votingId);
        mongoTemplate.save(voting).block();

        Mono<Voting> votingMono = votingDomainRepository.fetchById(votingId);

        StepVerifier.create(votingMono)
                .expectNextMatches(actual -> actual.getId().equals(votingId))
                .verifyComplete();
    }

    @Test
    void fetchById_shouldThrowNotFoundException_whenVotingDoesNotExist() {
        final var votingId = ObjectId.get();

        Mono<Voting> votingMono = votingDomainRepository.fetchById(votingId);

        StepVerifier.create(votingMono)
                .expectError(VotingException.NotFoundVotingException.class);
    }

    @Test
    void fetchByDomainObject_shouldReturnOne_whenExists() {
        final var votingId = ObjectId.get();
        final var domainObjectId = UUID.randomUUID().toString();
        final var domainObjectType = "TEST_TYPE";

        Voting voting = new Voting(domainObjectId, domainObjectType, votingId);
        mongoTemplate.save(voting).block();

        Mono<Voting> votingMono = votingDomainRepository.fetchByDomainObject(domainObjectId, domainObjectType);

        StepVerifier.create(votingMono)
                .expectNextMatches(actual -> actual.getId().equals(votingId))
                .verifyComplete();
    }

    @Test
    void fetchByDomainObject_shouldThrowNotFoundException_whenVotingDoesNotExist() {
        final var domainObjectId = UUID.randomUUID().toString();
        final var domainObjectType = "TEST_TYPE";

        Mono<Voting> votingMono = votingDomainRepository.fetchByDomainObject(domainObjectId, domainObjectType);

        StepVerifier.create(votingMono)
                .expectError(VotingException.NotFoundVotingException.class);
    }
}