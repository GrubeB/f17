package pl.app.comment.application.port.in;


import jakarta.validation.Valid;
import pl.app.comment.application.port.in.command.RemoveUserVoteRequestCommand;

public interface RemoveUserVoteRequestUseCase {
    void removeUserVote(@Valid RemoveUserVoteRequestCommand command);
}
