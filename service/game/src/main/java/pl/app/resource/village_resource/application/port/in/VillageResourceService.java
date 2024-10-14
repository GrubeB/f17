package pl.app.resource.village_resource.application.port.in;

import pl.app.resource.village_resource.application.domain.VillageResource;
import reactor.core.publisher.Mono;

public interface VillageResourceService {
    Mono<VillageResource> crate(VillageResourceCommand.CreateVillageResourceCommand command);

    Mono<VillageResource> add(VillageResourceCommand.AddResourceCommand command);

    Mono<VillageResource> subtract(VillageResourceCommand.SubtractResourceCommand command);

    Mono<VillageResource> change(VillageResourceCommand.ChangeMaxResourceCommand command);
}
