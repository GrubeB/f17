package pl.app.comment.application.port.in;


import jakarta.validation.Valid;
import pl.app.comment.application.port.in.VotingCommand.CreateVotingCommand;
import pl.app.voting.application.domain.Voting;
import reactor.core.publisher.Mono;

public interface CreateVotingUseCase {
    Mono<Voting> createVoting(@Valid CreateVotingCommand command);
}
