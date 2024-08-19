package pl.app.comment.application.port.in;


import jakarta.validation.Valid;
import pl.app.comment.application.port.in.VotingCommand.RemoveUserVoteCommand;
import pl.app.voting.application.domain.Voting;
import reactor.core.publisher.Mono;

public interface RemoveUserVoteUseCase {
    Mono<Voting> removeUserVote(@Valid RemoveUserVoteCommand command);
}
