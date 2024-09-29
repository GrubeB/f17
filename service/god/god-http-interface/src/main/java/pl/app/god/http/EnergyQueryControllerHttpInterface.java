package pl.app.god.http;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import pl.app.common.shared.config.ResponsePage;
import pl.app.energy.application.domain.EnergyException;
import pl.app.energy.query.dto.EnergyDto;
import pl.app.god.query.dto.GodAggregateDto;
import reactor.core.publisher.Mono;

@HttpExchange(
        url = "gods/{godId}/energies",
        accept = MediaType.APPLICATION_JSON_VALUE,
        contentType = MediaType.APPLICATION_JSON_VALUE
)
public interface EnergyQueryControllerHttpInterface {
    @GetExchange
    Mono<ResponseEntity<EnergyDto>> fetchByGodId(@PathVariable ObjectId godId);
}
