package pl.app.trader.adapter.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.app.trader.application.port.in.TraderCommand;
import pl.app.trader.application.port.in.TraderService;
import pl.app.trader.dto.TraderDto;
import pl.app.trader.query.TraderQueryService;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(TraaderRestController.resourcePath)
@RequiredArgsConstructor
class TraaderRestController {
    public static final String resourceName = "traders";
    public static final String resourcePath = "/api/v1/" + resourceName;

    private final TraderService service;
    private final TraderQueryService queryService;


    @PostMapping("/{godId}/renew")
    public Mono<ResponseEntity<TraderDto>> renew(@PathVariable ObjectId godId,
                                                                    @RequestBody TraderCommand.RenewItemsCommand command) {
        command.setGodId(godId);
        return service.renew(command)
                .flatMap(domain -> queryService.fetchByGodId(domain.getGodId()))
                .map(ResponseEntity::ok);
    }

    @PostMapping("/{godId}/buy")
    public Mono<ResponseEntity<TraderDto>> buy(@PathVariable ObjectId godId,
                                                     @RequestBody TraderCommand.BuyItemCommand command) {
        command.setGodId(godId);
        return service.buy(command)
                .flatMap(domain -> queryService.fetchByGodId(domain.getGodId()))
                .map(ResponseEntity::ok);
    }
    @PostMapping("/{godId}/sell")
    public Mono<ResponseEntity<TraderDto>> sell(@PathVariable ObjectId godId,
                                               @RequestBody TraderCommand.SellItemCommand command) {
        command.setGodId(godId);
        return service.sell(command)
                .flatMap(domain -> queryService.fetchByGodId(domain.getGodId()))
                .map(ResponseEntity::ok);
    }
}
