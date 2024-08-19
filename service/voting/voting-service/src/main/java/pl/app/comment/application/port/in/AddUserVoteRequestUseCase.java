package pl.app.comment.application.port.in;


import jakarta.validation.Valid;
import pl.app.comment.application.port.in.VotingCommand.AddUserVoteRequestCommand;
import reactor.core.publisher.Mono;

public interface AddUserVoteRequestUseCase {
    Mono<Void> addUserVoteRequest(@Valid AddUserVoteRequestCommand command);
}
