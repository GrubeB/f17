package pl.app.comment.application.port.in;


import jakarta.validation.Valid;
import pl.app.comment.application.domain.Voting;
import pl.app.comment.application.port.in.command.CreateVotingCommand;

public interface CreateVotingUseCase {
    Voting createVoting(@Valid CreateVotingCommand command);
}
