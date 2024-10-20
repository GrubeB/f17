package pl.app.unit.recruiter.adapter.in;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;
import pl.app.unit.recruiter.application.domain.Recruiter;
import pl.app.unit.recruiter.application.port.in.RecruiterCommand;
import pl.app.unit.recruiter.application.port.in.RecruiterService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
class UpcomingRecruitRequestsManager {
    private final RecruiterService recruiterService;
    private final Sinks.Many<Recruiter> sink = Sinks.many().multicast().onBackpressureBuffer();
    private final Flux<Recruiter> flux = sink.asFlux();

    private final Set<ObjectId> recruiterIds = ConcurrentHashMap.newKeySet();

    public UpcomingRecruitRequestsManager(RecruiterService recruiterService) {
        this.recruiterService = recruiterService;

        flux
                .filter(recruiter -> recruiter.getFirstRequest().isPresent())
                .flatMap(builder -> {
                    Instant toDate = builder.getFirstRequest().get().getTo();
                    return Mono.delay(Duration.between(Instant.now(), toDate))
                            .flatMap(unused -> this.recruiterService.finish(new RecruiterCommand.FinishRecruitRequestCommand(builder.getVillageId())))
                            .doFinally(signalType -> removeRecruiterId(builder.getVillageId()))
                            .subscribeOn(Schedulers.parallel());
                })
                .subscribe();
    }

    public void addRecruiter(Recruiter recruiter) {
        if (recruiterIds.add(recruiter.getVillageId())) {
            sink.tryEmitNext(recruiter);
        }
    }

    private void removeRecruiterId(ObjectId villageId) {
        recruiterIds.remove(villageId);
    }
}
