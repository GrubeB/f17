package pl.app.village.village_effect.adapter.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.app.village.village_effect.application.domain.VillageEffect;
import pl.app.village.village_effect.application.port.in.VillageEffectCommand;
import pl.app.village.village_effect.application.port.in.VillageEffectDomainRepository;
import pl.app.village.village_effect.application.port.in.VillageEffectService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
class UpcomingExpiredEffectManager {
    private final VillageEffectService villageEffectService;
    private final Sinks.Many<VillageEffect> sink = Sinks.many().multicast().onBackpressureBuffer();
    private final Flux<VillageEffect> flux = sink.asFlux();

    private final Set<ObjectId> ids = ConcurrentHashMap.newKeySet();

    public UpcomingExpiredEffectManager(VillageEffectService villageEffectService) {
        this.villageEffectService = villageEffectService;

        flux
                .flatMap(e -> {
                    Optional<VillageEffect.Effect> firstEffect = e.getEffects().stream().min(Comparator.comparing(VillageEffect.Effect::getTo));
                    if (firstEffect.isEmpty()) {
                        return Mono.empty();
                    }
                    Instant toDate = firstEffect.get().getTo();
                    return Mono.delay(Duration.between(Instant.now(), toDate))
                            .flatMap(unused -> this.villageEffectService.remove(new VillageEffectCommand.RemoveInvalidEffectsCommand(e.getVillageId())))
                            .doFinally(signalType -> removeId(e.getVillageId()))
                            .subscribeOn(Schedulers.parallel());
                })
                .subscribe();
    }

    public void add(VillageEffect e) {
        if (ids.add(e.getVillageId())) {
            sink.tryEmitNext(e);
        }
    }

    private void removeId(ObjectId villageId) {
        ids.remove(villageId);
    }

    @ConditionalOnProperty(value = "app.schedulers.enable", matchIfMissing = true)
    @Component
    @RequiredArgsConstructor
    public static class UpcomingExpiredEffectManagerDataPuller {
        private static final Logger logger = LoggerFactory.getLogger(UpcomingExpiredEffectManagerDataPuller.class);

        private final UpcomingExpiredEffectManager upcomingExpiredEffectManager;
        private final VillageEffectDomainRepository villageEffectDomainRepository;

        @Scheduled(cron = "*/30 * * ? * *")
        public void addRecruiter() {
            logger.trace("adding upcoming expired effects");
            var startTime = Instant.now();
            villageEffectDomainRepository.fetchVillageEffectWithEnding(Duration.ofSeconds(31))
                    .doOnNext(upcomingExpiredEffectManager::add)
                    .doOnComplete(() -> logger.trace("added upcoming expired effects - {}", Duration.between(startTime, Instant.now())))
                    .subscribe();
        }
    }

}
