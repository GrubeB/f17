package pl.app.voting.adapter.in;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.app.voting.application.port.in.AddUserVoteRequestUseCase;
import pl.app.voting.application.port.in.RemoveUserVoteRequestUseCase;
import pl.app.voting.application.port.in.VotingCommand;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(VoteRestController.resourcePath)
@RequiredArgsConstructor
class VoteRestController {
    public static final String resourceName = "votes";
    public static final String resourcePath = "/api/v1/" + resourceName;

    private final AddUserVoteRequestUseCase addUserVoteRequestUseCase;
    private final RemoveUserVoteRequestUseCase removeUserVoteRequestUseCase;

    @PostMapping
    public Mono<ResponseEntity<Void>> addUserVoteRequest(@RequestBody VotingCommand.AddUserVoteRequestCommand command) {
        return addUserVoteRequestUseCase.addUserVoteRequest(command)
                .then(Mono.just(ResponseEntity.accepted().build()));
    }

    @DeleteMapping
    public Mono<ResponseEntity<Void>> removeUserVote(@RequestBody VotingCommand.RemoveUserVoteRequestCommand command) {
        return removeUserVoteRequestUseCase.removeUserVote(command)
                .then(Mono.just(ResponseEntity.accepted().build()));
    }
}
