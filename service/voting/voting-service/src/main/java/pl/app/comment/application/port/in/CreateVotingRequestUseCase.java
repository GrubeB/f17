package pl.app.comment.application.port.in;


import jakarta.validation.Valid;
import org.bson.types.ObjectId;
import pl.app.comment.application.port.in.command.CreateVotingRequestCommand;
import reactor.core.publisher.Mono;

public interface CreateVotingRequestUseCase {
    Mono<ObjectId> createVotingRequest(@Valid CreateVotingRequestCommand command);
}
