package pl.app.building.builder.adapter.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.app.building.builder.application.domain.Builder;
import pl.app.building.builder.application.port.in.BuilderCommand;
import pl.app.building.builder.application.port.in.BuilderDomainRepository;
import pl.app.building.builder.application.port.in.BuilderService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
class FinishConstructionPolicy {
    private final BuilderService builderService;
    private final Sinks.Many<Builder> sink = Sinks.many().multicast().onBackpressureBuffer();
    private final Flux<Builder> flux = sink.asFlux();

    private final Set<ObjectId> builderIds = ConcurrentHashMap.newKeySet();

    public FinishConstructionPolicy(BuilderService builderService) {
        this.builderService = builderService;

        flux
                .filter(builder -> builder.getFirstConstruct().isPresent())
                .flatMap(builder -> {
                    Instant toDate = builder.getFirstConstruct().get().getTo();
                    return Mono.delay(Duration.between(Instant.now(), toDate))
                            .flatMap(unused -> this.builderService.finish(new BuilderCommand.FinishBuildingConstructCommand(builder.getVillageId())))
                            .doFinally(signalType -> removeBuilderId(builder.getVillageId()))
                            .subscribeOn(Schedulers.parallel());
                })
                .subscribe();
    }

    public void addBuilder(Builder builder) {
        if (builderIds.add(builder.getVillageId())) {
            sink.tryEmitNext(builder);
        }
    }

    private void removeBuilderId(ObjectId villageId) {
        builderIds.remove(villageId);
    }

    @ConditionalOnProperty(value = "app.schedulers.enable", matchIfMissing = true)
    @Component
    @RequiredArgsConstructor
    public static class UpcomingConstructsManagerDataPuller {
        private static final Logger logger = LoggerFactory.getLogger(UpcomingConstructsManagerDataPuller.class);

        private final FinishConstructionPolicy finishConstructionPolicy;
        private final BuilderDomainRepository builderDomainRepository;

        @Scheduled(cron = "*/30 * * ? * *")
        public void addBuilder() {
            logger.trace("adding upcoming constructs");
            builderDomainRepository.fetchBuildersWithConstructEnding(Duration.ofSeconds(31))
                    .doOnNext(finishConstructionPolicy::addBuilder)
                    .doOnComplete(() -> logger.trace("added upcoming constructs"))
                    .subscribe();
        }
    }

}
