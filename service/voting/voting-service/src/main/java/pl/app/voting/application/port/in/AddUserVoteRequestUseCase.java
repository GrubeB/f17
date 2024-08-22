package pl.app.voting.application.port.in;


import jakarta.validation.Valid;
import pl.app.voting.application.port.in.VotingCommand.AddUserVoteRequestCommand;
import reactor.core.publisher.Mono;

public interface AddUserVoteRequestUseCase {
    Mono<Void> addUserVoteRequest(@Valid AddUserVoteRequestCommand command);
}
