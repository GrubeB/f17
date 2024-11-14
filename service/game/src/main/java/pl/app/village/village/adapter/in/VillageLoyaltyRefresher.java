package pl.app.village.village.adapter.in;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.app.village.village.application.port.in.VillageCommand;
import pl.app.village.village.application.port.in.VillageDomainRepository;
import pl.app.village.village.application.port.in.VillageService;

import java.time.Duration;
import java.time.Instant;

@ConditionalOnProperty(value = "app.schedulers.enable", matchIfMissing = true)
@Component
@RequiredArgsConstructor
class VillageLoyaltyRefresher {
    private static final Logger logger = LoggerFactory.getLogger(VillageLoyaltyRefresher.class);

    private final VillageDomainRepository villageDomainRepository;
    private final VillageService villageService;

    @Scheduled(cron = "0 */5 * ? * *")
    public void refresh() {
        logger.trace("refreshing village loyalty");
        var startTime = Instant.now();
        villageDomainRepository.fetchVillagesWithoutMaxLoyalty()
                .flatMap(village -> villageService.refreshLoyalty(new VillageCommand.RefreshLoyaltyCommand(village.getId())))
                .doOnComplete(() -> logger.trace("refreshed village loyalty - {}", Duration.between(startTime, Instant.now())))
                .subscribe();
    }
}
