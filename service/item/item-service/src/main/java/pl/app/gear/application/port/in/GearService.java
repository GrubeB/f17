package pl.app.gear.application.port.in;

import pl.app.gear.aplication.domain.Gear;
import reactor.core.publisher.Mono;

public interface GearService {
    Mono<Gear> crate(GearCommand.CreateGearCommand command);

    Mono<Gear> remove(GearCommand.RemoveGearCommand command);

    Mono<Gear> setItem(GearCommand.SetItemCommand command);

    Mono<Gear> removeItem(GearCommand.RemoveItemCommand command);
}
