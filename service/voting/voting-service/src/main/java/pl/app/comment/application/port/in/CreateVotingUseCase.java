package pl.app.comment.application.port.in;


import jakarta.validation.Valid;
import pl.app.voting.application.domain.Voting;
import pl.app.comment.application.port.in.command.CreateVotingCommand;
import reactor.core.publisher.Mono;

public interface CreateVotingUseCase {
    Mono<Voting> createVoting(@Valid CreateVotingCommand command);
}
