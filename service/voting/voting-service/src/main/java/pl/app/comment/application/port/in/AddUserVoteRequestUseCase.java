package pl.app.comment.application.port.in;


import jakarta.validation.Valid;
import pl.app.comment.application.port.in.command.AddUserVoteRequestCommand;

public interface AddUserVoteRequestUseCase {
    void addUserVoteRequest(@Valid AddUserVoteRequestCommand command);
}
