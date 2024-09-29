package pl.app.god.http;

import org.bson.types.ObjectId;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import pl.app.energy.application.port.in.EnergyCommand;
import pl.app.energy.query.dto.EnergyDto;
import reactor.core.publisher.Mono;

@HttpExchange(
        url = "gods/{godId}/energies",
        accept = MediaType.APPLICATION_JSON_VALUE,
        contentType = MediaType.APPLICATION_JSON_VALUE
)
public interface EnergyControllerHttpInterface {
    @PostExchange("/add")
    Mono<ResponseEntity<EnergyDto>> addEnergy(@PathVariable ObjectId godId,
                                              @RequestBody EnergyCommand.AddEnergyCommand command);

    @PostExchange("/subtract")
    Mono<ResponseEntity<EnergyDto>> subtractEnergy(@PathVariable ObjectId godId,
                                                   @RequestBody EnergyCommand.SubtractEnergyCommand command);
}
