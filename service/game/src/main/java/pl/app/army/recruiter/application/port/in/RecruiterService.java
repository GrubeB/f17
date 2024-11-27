package pl.app.army.recruiter.application.port.in;

import pl.app.army.recruiter.application.domain.Recruiter;
import reactor.core.publisher.Mono;

public interface RecruiterService {
    Mono<Recruiter> crate(RecruiterCommand.CreateRecruiterCommand command);

    Mono<Recruiter> add(RecruiterCommand.AddRecruitRequestCommand command);

    Mono<Recruiter> start(RecruiterCommand.StartRecruitRequestCommand command);

    Mono<Recruiter> finish(RecruiterCommand.FinishRecruitRequestCommand command);

    Mono<Recruiter> cancel(RecruiterCommand.CancelRecruitRequestCommand command);

    Mono<Recruiter> reject(RecruiterCommand.RejectRecruitRequestCommand command);
}
