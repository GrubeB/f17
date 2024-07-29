package pl.app.comment.adapter.in;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.app.comment.application.port.in.AddUserVoteRequestUseCase;
import pl.app.comment.application.port.in.RemoveUserVoteRequestUseCase;
import pl.app.comment.application.port.in.command.AddUserVoteRequestCommand;
import pl.app.comment.application.port.in.command.RemoveUserVoteRequestCommand;

@RestController
@RequestMapping(VoteRestController.resourcePath)
@RequiredArgsConstructor
class VoteRestController {
    public static final String resourceName = "votes";
    public static final String resourcePath = "/api/v1/" + resourceName;

    private final AddUserVoteRequestUseCase addUserVoteRequestUseCase;
    private final RemoveUserVoteRequestUseCase removeUserVoteRequestUseCase;

    @PostMapping
    public ResponseEntity<Void> addUserVoteRequest(@RequestBody AddUserVoteRequestCommand command) {
        addUserVoteRequestUseCase.addUserVoteRequest(command);
        return ResponseEntity
                .accepted()
                .build();
    }

    @DeleteMapping
    public ResponseEntity<Void> removeUserVote(@RequestBody RemoveUserVoteRequestCommand command) {
        removeUserVoteRequestUseCase.removeUserVote(command);
        return ResponseEntity
                .accepted()
                .build();
    }
}
