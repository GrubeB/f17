package pl.app.energy.application.port.in;

import pl.app.energy.application.domain.Energy;
import pl.app.god.application.domain.God;
import pl.app.god.application.port.in.GodCommand;
import reactor.core.publisher.Mono;

public interface EnergyService {
    Mono<Energy> create(EnergyCommand.CreateEnergyCommand command);
    Mono<Energy> refreshEnergy(EnergyCommand.RefreshEnergyCommand command);
    Mono<Energy> addEnergy(EnergyCommand.AddEnergyCommand command);
    Mono<Energy> subtractEnergy(EnergyCommand.SubtractEnergyCommand command);
}
