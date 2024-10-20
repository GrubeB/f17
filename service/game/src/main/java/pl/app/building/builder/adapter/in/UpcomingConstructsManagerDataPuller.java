package pl.app.building.builder.adapter.in;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.app.building.builder.application.port.in.BuilderDomainRepository;

import java.time.Duration;

@Component
@RequiredArgsConstructor
class UpcomingConstructsManagerDataPuller {
    private static final Logger logger = LoggerFactory.getLogger(UpcomingConstructsManagerDataPuller.class);

    private final UpcomingConstructsManager upcomingConstructsManager;
    private final BuilderDomainRepository builderDomainRepository;

    @Scheduled(cron = "*/30 * * ? * *")
    public void addBuilder() {
        logger.trace("adding upcoming constructs");
        builderDomainRepository.fetchBuildersWithConstructEnding(Duration.ofSeconds(31))
                .doOnNext(upcomingConstructsManager::addBuilder)
                .doOnComplete(() -> logger.trace("added upcoming constructs"))
                .subscribe();
    }
}
