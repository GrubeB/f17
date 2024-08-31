package pl.app.recruitment.adapter.in;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.app.recruitment.application.port.in.RecruitmentCommand;
import pl.app.recruitment.application.port.in.RecruitmentResponse;
import pl.app.recruitment.application.port.in.RecruitmentService;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(RecruitmentController.resourcePath)
@RequiredArgsConstructor
class RecruitmentController {
    public static final String resourceName = "recruitments";
    public static final String resourcePath = "/api/v1/" + resourceName;

    private final RecruitmentService service;

    @PostMapping
    Mono<ResponseEntity<RecruitmentResponse.RecruitmentAnnouncementPostedResponse>> create(@RequestBody RecruitmentCommand.PostRecruitmentAnnouncementCommand command) {
        return service.post(command)
                .map(ResponseEntity::ok);
    }
}
