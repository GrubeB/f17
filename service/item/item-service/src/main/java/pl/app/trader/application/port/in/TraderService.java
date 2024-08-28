package pl.app.trader.application.port.in;

import pl.app.item.application.domain.Item;
import pl.app.trader.application.domain.Trader;
import reactor.core.publisher.Mono;

public interface TraderService {
    Mono<Trader> create(TraderCommand.CrateTraderCommand command);
    Mono<Trader> renew(TraderCommand.RenewItemsCommand command);
    Mono<Trader> buy(TraderCommand.BuyItemCommand command);
}
