package pl.app.account.http;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import pl.app.god.application.port.in.GodCommand;
import pl.app.god.query.dto.GodDto;
import reactor.core.publisher.Mono;

@HttpExchange(
        url = "gods/{godId}/monies",
        accept = MediaType.APPLICATION_JSON_VALUE,
        contentType = MediaType.APPLICATION_JSON_VALUE
)
public interface GodMoneyControllerHttpInterface {
    @PostExchange("/add")
    Mono<ResponseEntity<GodDto>> addMoney(@RequestBody GodCommand.AddMoneyCommand command, @PathVariable("godId") String godId);

    @PostExchange("/subtract")
    Mono<ResponseEntity<GodDto>> subtractMoney(@RequestBody GodCommand.SubtractMoneyCommand command, @PathVariable("godId") String godId);

}
