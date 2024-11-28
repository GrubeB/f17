package pl.app.village.loyalty.adapter.in;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.app.village.loyalty.application.port.in.VillageLoyaltyCommand;
import pl.app.village.loyalty.application.port.in.VillageLoyaltyDomainRepository;
import pl.app.village.loyalty.application.port.in.VillageLoyaltyService;

import java.time.Duration;
import java.time.Instant;

@ConditionalOnProperty(value = "app.schedulers.enable", matchIfMissing = true)
@Component
@RequiredArgsConstructor
class RefreshLoyaltyPolicy {
    private static final Logger logger = LoggerFactory.getLogger(RefreshLoyaltyPolicy.class);

    private final VillageLoyaltyDomainRepository domainRepository;
    private final VillageLoyaltyService service;

    @Scheduled(cron = "0 */5 * ? * *")
    public void refresh() {
        logger.trace("refreshing village loyalty");
        var startTime = Instant.now();
        domainRepository.fetchVillagesWithoutMaxLoyalty()
                .flatMap(village -> service.refresh(new VillageLoyaltyCommand.RefreshLoyaltyCommand(village.getVillageId())))
                .doOnComplete(() -> logger.trace("refreshed village loyalty - {}", Duration.between(startTime, Instant.now())))
                .subscribe();
    }
}
