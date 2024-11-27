package pl.app.army.recruiter.adapter.in;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.app.army.recruiter.application.port.in.RecruiterCommand;
import pl.app.army.recruiter.application.port.in.RecruiterDomainRepository;
import pl.app.army.recruiter.application.port.in.RecruiterService;
import pl.app.building.builder.application.port.in.BuilderCommand;
import pl.app.building.builder.application.port.in.BuilderDomainRepository;
import pl.app.building.builder.application.port.in.BuilderService;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

// searches database, and run StartRecruitRequestCommand when event base solution(StartRecruitRequestPolicy) wouldn't work for some reason
@ConditionalOnProperty(value = "app.schedulers.enable", matchIfMissing = true)
@Component
@RequiredArgsConstructor
class StartRecruitRequestPolicyBackupProcessor {
    private final RecruiterDomainRepository recruiterDomainRepository;
    private final RecruiterService service;

    @Scheduled(cron = "*/5 * * ? * *")
    public void addBuilder() {
        recruiterDomainRepository.fetchRecruiterWithRequestStarting(Duration.of(-5, ChronoUnit.SECONDS))
                .flatMap(e -> service.start(new RecruiterCommand.StartRecruitRequestCommand(e.getVillageId())))
                .subscribe();
    }
}