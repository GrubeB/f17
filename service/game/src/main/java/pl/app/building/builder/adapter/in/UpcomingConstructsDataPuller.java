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
class UpcomingConstructsDataPuller {
    private static final Logger logger = LoggerFactory.getLogger(UpcomingConstructsDataPuller.class);

    private final UpcomingConstructs upcomingConstructs;
    private final BuilderDomainRepository builderDomainRepository;

    @Scheduled(cron = "*/30 * * ? * *")
    public void refreshEnergy() {
        logger.trace("adding upcoming constructs");
        builderDomainRepository.fetchBuildersWithConstructEnding(Duration.ofSeconds(31))
                .doOnNext(upcomingConstructs::addBuilder)
                .doOnComplete(() -> logger.trace("added upcoming constructs"))
                .subscribe();
    }
}
