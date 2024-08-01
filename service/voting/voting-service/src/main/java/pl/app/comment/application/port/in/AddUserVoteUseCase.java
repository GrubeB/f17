package pl.app.comment.application.port.in;


import jakarta.validation.Valid;
import pl.app.comment.application.port.in.command.AddUserVoteCommand;

public interface AddUserVoteUseCase {
    void addUserVote(@Valid AddUserVoteCommand command);
}
