package pl.app.god.adapter.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.app.god.application.port.in.GodCommand;
import pl.app.god.application.port.in.GodMoneyService;
import pl.app.god.query.GodQueryService;
import pl.app.god.query.dto.GodDto;
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
    Mono<ResponseEntity<GodDto>> addMoney(@PathVariable ObjectId godId,
                                          @RequestBody GodCommand.AddMoneyCommand command) {
        return service.addMoney(command)
                .flatMap(domain -> queryService.fetchById(domain.getId()))
                .map(ResponseEntity::ok);
    }

    @PostMapping("/subtract")
    Mono<ResponseEntity<GodDto>> subtractMoney(@PathVariable ObjectId godId,
                                               @RequestBody GodCommand.SubtractMoneyCommand command) {
        return service.subtractMoney(command)
                .flatMap(domain -> queryService.fetchById(domain.getId()))
                .map(ResponseEntity::ok);
    }
}
