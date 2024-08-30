package pl.app.recruitment.application.port.in;

import reactor.core.publisher.Mono;


public interface RecruitmentService {
    Mono<Void> post(RecruitmentCommand.PostRecruitmentAnnouncementCommand command);
}
