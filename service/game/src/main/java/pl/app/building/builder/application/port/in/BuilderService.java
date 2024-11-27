package pl.app.building.builder.application.port.in;

import pl.app.building.builder.application.domain.Builder;
import reactor.core.publisher.Mono;

public interface BuilderService {
    Mono<Builder> crate(BuilderCommand.CreateBuilderCommand command);
    Mono<Builder> add(BuilderCommand.AddBuildingToConstructCommand command);
    Mono<Builder> start(BuilderCommand.StartBuildingToConstructCommand command);
    Mono<Builder> finish(BuilderCommand.FinishBuildingConstructCommand command);
    Mono<Builder> cancel(BuilderCommand.CancelBuildingConstructCommand command);
    Mono<Builder> reject(BuilderCommand.RejectBuildingConstructCommand command);
}
