package pl.app.recruitment.application.port.in;

import reactor.core.publisher.Mono;


public interface RecruitmentService {
    Mono<RecruitmentResponse.RecruitmentAnnouncementPostedResponse> post(RecruitmentCommand.PostRecruitmentAnnouncementCommand command);
}
