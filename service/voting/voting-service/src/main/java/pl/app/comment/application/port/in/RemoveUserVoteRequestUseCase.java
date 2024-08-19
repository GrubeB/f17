package pl.app.comment.application.port.in;


import jakarta.validation.Valid;
import pl.app.comment.application.port.in.VotingCommand.RemoveUserVoteRequestCommand;
import reactor.core.publisher.Mono;

public interface RemoveUserVoteRequestUseCase {
    Mono<Void> removeUserVote(@Valid RemoveUserVoteRequestCommand command);
}
