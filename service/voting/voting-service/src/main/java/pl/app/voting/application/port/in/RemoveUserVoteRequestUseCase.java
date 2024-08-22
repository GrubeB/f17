package pl.app.voting.application.port.in;


import jakarta.validation.Valid;
import pl.app.voting.application.port.in.VotingCommand.RemoveUserVoteRequestCommand;
import reactor.core.publisher.Mono;

public interface RemoveUserVoteRequestUseCase {
    Mono<Void> removeUserVote(@Valid RemoveUserVoteRequestCommand command);
}
