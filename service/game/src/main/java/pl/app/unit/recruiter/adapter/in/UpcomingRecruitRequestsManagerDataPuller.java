package pl.app.unit.recruiter.adapter.in;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.app.unit.recruiter.application.port.in.RecruiterDomainRepository;

import java.time.Duration;
import java.time.Instant;

@Component
@RequiredArgsConstructor
class UpcomingRecruitRequestsManagerDataPuller {
    private static final Logger logger = LoggerFactory.getLogger(UpcomingRecruitRequestsManagerDataPuller.class);

    private final UpcomingRecruitRequestsManager upcomingRecruitRequestsManager;
    private final RecruiterDomainRepository recruiterDomainRepository;

    @Scheduled(cron = "*/30 * * ? * *")
    public void addRecruiter() {
        logger.trace("adding upcoming recruit requests");
        var startTime = Instant.now();
        recruiterDomainRepository.fetchRecruiterWithRequestEnding(Duration.ofSeconds(31))
                .doOnNext(upcomingRecruitRequestsManager::addRecruiter)
                .doOnComplete(() -> logger.trace("added upcoming recruit requests - {}", Duration.between(startTime, Instant.now())))
                .subscribe();
    }
}
