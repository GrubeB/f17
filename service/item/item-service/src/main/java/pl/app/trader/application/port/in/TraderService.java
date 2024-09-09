package pl.app.trader.application.port.in;

import pl.app.trader.application.domain.Trader;
import reactor.core.publisher.Mono;

public interface TraderService {
    Mono<Trader> create(TraderCommand.CrateTraderCommand command);

    Mono<Trader> renew(TraderCommand.RenewItemsCommand command);

    Mono<Trader> buy(TraderCommand.BuyItemCommand command);

    Mono<Trader> sell(TraderCommand.SellItemCommand command);
}
