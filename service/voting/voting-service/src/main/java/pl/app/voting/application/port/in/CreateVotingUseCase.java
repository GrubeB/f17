package pl.app.voting.application.port.in;


import jakarta.validation.Valid;
import pl.app.voting.application.domain.Voting;
import reactor.core.publisher.Mono;

public interface CreateVotingUseCase {
    Mono<Voting> createVoting(@Valid VotingCommand.CreateVotingCommand command);
}
