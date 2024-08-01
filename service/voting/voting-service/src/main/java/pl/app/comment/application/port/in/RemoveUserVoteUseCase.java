package pl.app.comment.application.port.in;


import jakarta.validation.Valid;
import pl.app.comment.application.port.in.command.RemoveUserVoteCommand;

public interface RemoveUserVoteUseCase {
    void removeUserVote(@Valid RemoveUserVoteCommand command);
}
