package pl.app.unit.recruiter.application.port.in;

import pl.app.unit.recruiter.application.domain.Recruiter;
import reactor.core.publisher.Mono;

public interface RecruiterService {
    Mono<Recruiter> crate(RecruiterCommand.CreateRecruiterCommand command);

    Mono<Recruiter> add(RecruiterCommand.AddRecruitRequestCommand command);

    Mono<Recruiter> remove(RecruiterCommand.RemoveRecruitRequestCommand command);

    Mono<Recruiter> finish(RecruiterCommand.FinishRecruitRequestCommand command);
}
