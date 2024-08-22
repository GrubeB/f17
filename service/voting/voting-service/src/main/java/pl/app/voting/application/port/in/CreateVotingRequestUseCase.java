package pl.app.voting.application.port.in;


import jakarta.validation.Valid;
import org.bson.types.ObjectId;
import pl.app.voting.application.port.in.VotingCommand.CreateVotingRequestCommand;
import reactor.core.publisher.Mono;

public interface CreateVotingRequestUseCase {
    Mono<ObjectId> createVotingRequest(@Valid CreateVotingRequestCommand command);
}
