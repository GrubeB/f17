package pl.app.comment.application.port.in;


import jakarta.validation.Valid;
import org.bson.types.ObjectId;
import pl.app.comment.application.port.in.command.CreateVotingRequestCommand;

public interface CreateVotingRequestUseCase {
    ObjectId createVotingRequest(@Valid CreateVotingRequestCommand command);
}
