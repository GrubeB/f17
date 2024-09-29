package pl.app.god.http;

import org.bson.types.ObjectId;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
    Mono<ResponseEntity<GodDto>> addMoney(@PathVariable ObjectId godId,
                                          @RequestBody GodCommand.AddMoneyCommand command);

    @PostExchange("/subtract")
    Mono<ResponseEntity<GodDto>> subtractMoney(@PathVariable ObjectId godId,
                                               @RequestBody GodCommand.SubtractMoneyCommand command);
}
