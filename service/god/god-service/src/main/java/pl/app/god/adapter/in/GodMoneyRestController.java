package pl.app.god.adapter.in;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import pl.app.common.shared.model.Money;
import pl.app.god.application.port.in.GodCommand;
import pl.app.god.application.port.in.GodMoneyService;
import pl.app.god.application.port.in.GodService;
import pl.app.god.query.GodQueryService;
import pl.app.god.query.dto.GodDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(GodMoneyRestController.resourcePath)
@RequiredArgsConstructor
class GodMoneyRestController {
    public static final String resourceName = "monies";
    public static final String resourcePath = "/api/v1/gods/{godId}/" + resourceName;

    private final GodMoneyService service;
    private final GodQueryService queryService;


    @PostMapping("/add")
    public Mono<ResponseEntity<GodDto>> addMoney(@RequestBody GodCommand.AddMoneyCommand command) {
        return service.addMoney(command)
                .flatMap(domain -> queryService.fetchById(domain.getId()))
                .map(ResponseEntity::ok);
    }
    @PostMapping("/subtract")
    public Mono<ResponseEntity<GodDto>> subtractMoney(ServerWebExchange exchange, @RequestBody GodCommand.SubtractMoneyCommand command) {
        return service.subtractMoney(command)
                .flatMap(domain -> queryService.fetchById(domain.getId()))
                .map(ResponseEntity::ok);
    }
}
