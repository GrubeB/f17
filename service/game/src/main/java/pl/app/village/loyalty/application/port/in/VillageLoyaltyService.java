package pl.app.village.loyalty.application.port.in;

import pl.app.village.loyalty.application.domain.VillageLoyalty;
import reactor.core.publisher.Mono;

public interface VillageLoyaltyService {
    Mono<VillageLoyalty> create(VillageLoyaltyCommand.CreateVillageLoyaltyCommand command);

    Mono<VillageLoyalty> subtract(VillageLoyaltyCommand.SubtractLoyaltyCommand command);

    Mono<VillageLoyalty> refresh(VillageLoyaltyCommand.RefreshLoyaltyCommand command);

    Mono<VillageLoyalty> reset(VillageLoyaltyCommand.ResetLoyaltyCommand command);
}
