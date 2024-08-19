package pl.app.comment.adapter.in;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.app.comment.application.port.in.AddUserVoteRequestUseCase;
import pl.app.comment.application.port.in.RemoveUserVoteRequestUseCase;
import pl.app.comment.application.port.in.VotingCommand.AddUserVoteRequestCommand;
import pl.app.comment.application.port.in.VotingCommand.RemoveUserVoteRequestCommand;
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
    public Mono<ResponseEntity<Void>> addUserVoteRequest(@RequestBody AddUserVoteRequestCommand command) {
        return addUserVoteRequestUseCase.addUserVoteRequest(command)
                .then(Mono.just(ResponseEntity.accepted().build()));
    }

    @DeleteMapping
    public Mono<ResponseEntity<Void>> removeUserVote(@RequestBody RemoveUserVoteRequestCommand command) {
        return removeUserVoteRequestUseCase.removeUserVote(command)
                .then(Mono.just(ResponseEntity.accepted().build()));
    }
}
