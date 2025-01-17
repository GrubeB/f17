package pl.app.resource.village_resource.adapter.in;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.app.resource.village_resource.application.port.in.VillageResourceCommand;
import pl.app.resource.village_resource.application.port.in.VillageResourceDomainRepository;
import pl.app.resource.village_resource.application.port.in.VillageResourceService;

import java.time.Duration;
import java.time.Instant;

@ConditionalOnProperty(value = "app.schedulers.enable", matchIfMissing = true)
@Component
@RequiredArgsConstructor
class ResourceRefreshPolicy {
    private static final Logger logger = LoggerFactory.getLogger(ResourceRefreshPolicy.class);

    private final VillageResourceDomainRepository villageResourceDomainRepository;
    private final VillageResourceService villageResourceService;

    @Scheduled(cron = "*/30 * * ? * *")
    public void refresh() {
        logger.trace("refreshing village resources");
        var startTime = Instant.now();
        villageResourceDomainRepository.fetchAll()
                .flatMap(villageResource -> {
                    var command = new VillageResourceCommand.RefreshResourceCommand(villageResource.getVillageId());
                    return villageResourceService.refresh(command);
                })
                .doOnComplete(() -> logger.trace("refreshed village resources - {}", Duration.between(startTime, Instant.now())))
                .subscribe();
    }
}
