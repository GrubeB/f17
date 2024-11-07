package pl.app.attack.army_walk.adapter.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.app.attack.army_walk.domain.application.ArmyWalk;
import pl.app.attack.army_walk.domain.port.in.ArmyWalkCommand;
import pl.app.attack.army_walk.domain.port.in.ArmyWalkDomainRepository;
import pl.app.attack.army_walk.domain.port.in.ArmyWalkService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
class UpcomingArmyArrivalsManager {
    private final ArmyWalkService armyWalkService;
    private final Sinks.Many<ArmyWalk> sink = Sinks.many().multicast().onBackpressureBuffer();
    private final Flux<ArmyWalk> flux = sink.asFlux();

    private final Set<ObjectId> ids = ConcurrentHashMap.newKeySet();

    public UpcomingArmyArrivalsManager(ArmyWalkService armyWalkService) {
        this.armyWalkService = armyWalkService;

        flux
                .flatMap(armyWalk -> {
                    Instant toDate = armyWalk.getArriveDate();
                    return Mono.delay(Duration.between(Instant.now(), toDate))
                            .flatMap(unused -> this.armyWalkService.process(new ArmyWalkCommand.ProcessArmyArrivalCommand(armyWalk.getArmyWalkId())))
                            .doFinally(signalType -> removeId(armyWalk.getArmyWalkId()))
                            .subscribeOn(Schedulers.parallel());
                })
                .subscribe();
    }

    public void add(ArmyWalk armyWalk) {
        if (ids.add(armyWalk.getArmyWalkId())) {
            sink.tryEmitNext(armyWalk);
        }
    }

    private void removeId(ObjectId villageId) {
        ids.remove(villageId);
    }

    @Component
    @RequiredArgsConstructor
    public static class UpcomingArmyArrivalManagerDataPuller {
        private static final Logger logger = LoggerFactory.getLogger(UpcomingArmyArrivalManagerDataPuller.class);

        private final UpcomingArmyArrivalsManager upcomingArmyArrivalsManager;
        private final ArmyWalkDomainRepository armyWalkDomainRepository;

        @Scheduled(cron = "*/30 * * ? * *")
        public void addRecruiter() {
            logger.debug("adding upcoming army arrivals");
            armyWalkDomainRepository.fetchArmyWalkWithEnding(Duration.ofSeconds(31))
                    .doOnNext(upcomingArmyArrivalsManager::add)
                    .doOnComplete(() -> logger.debug("added upcoming army arrivals"))
                    .subscribe();
        }
    }

}
