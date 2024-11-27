package pl.app.building.builder.adapter.in;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.app.building.builder.application.port.in.BuilderCommand;
import pl.app.building.builder.application.port.in.BuilderDomainRepository;
import pl.app.building.builder.application.port.in.BuilderService;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

// searches database, and run StartBuildingToConstructCommand when event base solution(StartConstructionPolicy) wouldn't work for some reason
@ConditionalOnProperty(value = "app.schedulers.enable", matchIfMissing = true)
@Component
@RequiredArgsConstructor
class StartConstructionPolicyBackupProcessor {
    private final BuilderDomainRepository builderDomainRepository;
    private final BuilderService builderService;

    @Scheduled(cron = "*/5 * * ? * *")
    public void addBuilder() {
        builderDomainRepository.fetchBuildersWithConstructionStarting(Duration.of(-5, ChronoUnit.SECONDS))
                .flatMap(e -> builderService.start(new BuilderCommand.StartBuildingToConstructCommand(e.getVillageId())))
                .subscribe();
    }
}