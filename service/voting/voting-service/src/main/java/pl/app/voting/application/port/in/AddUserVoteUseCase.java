package pl.app.voting.application.port.in;


import jakarta.validation.Valid;
import pl.app.voting.application.port.in.VotingCommand.AddUserVoteCommand;
import pl.app.voting.application.domain.Voting;
import reactor.core.publisher.Mono;

public interface AddUserVoteUseCase {
    Mono<Voting> addUserVote(@Valid AddUserVoteCommand command);
}
