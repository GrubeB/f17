package pl.app.building.builder.adapter.in;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;
import pl.app.building.builder.application.domain.Builder;
import pl.app.building.builder.application.port.in.BuilderCommand;
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
class UpcomingConstructsManager {
    private final BuilderService builderService;
    private final Sinks.Many<Builder> sink = Sinks.many().multicast().onBackpressureBuffer();
    private final Flux<Builder> flux = sink.asFlux();

    private final Set<ObjectId> builderIds = ConcurrentHashMap.newKeySet();

    public UpcomingConstructsManager(BuilderService builderService) {
        this.builderService = builderService;

        flux
                .filter(builder -> builder.getFirstConstruct().isPresent())
                .flatMap(builder -> {
                    Instant toDate = builder.getFirstConstruct().get().getTo();
                    return Mono.delay(Duration.between(Instant.now(), toDate))
                            .flatMap(unused -> this.builderService.finish(new BuilderCommand.FinishConstructCommand(builder.getVillageId())))
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
}
