package pl.app.energy.adapter.in;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.app.energy.application.port.in.EnergyCommand;
import pl.app.energy.application.port.in.EnergyService;
import pl.app.god.query.GodQueryService;
import reactor.core.publisher.Flux;

@Component
@RequiredArgsConstructor
class RefreshEnergyScheduler {
    private static final Logger logger = LoggerFactory.getLogger(RefreshEnergyScheduler.class);

    private final EnergyService energyService;
    private final GodQueryService godQueryService;

    @Scheduled(cron = "0,30 * * ? * *")
    public void refreshEnergy() {
        logger.debug("refreshing energy for gods");
        godQueryService.fetchAll().flatMapMany(Flux::fromIterable)
                .map(god -> {
                    var command = new EnergyCommand.RefreshEnergyCommand(
                            god.getId()
                    );
                    return energyService.refreshEnergy(command);
                })
                .doOnComplete(() -> logger.debug("refreshed energy"))
                .subscribe();
    }
}
