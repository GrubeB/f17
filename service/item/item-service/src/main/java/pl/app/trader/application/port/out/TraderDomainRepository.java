package pl.app.trader.application.port.out;

import org.bson.types.ObjectId;
import pl.app.item.application.domain.Item;
import pl.app.item.application.domain.Outfit;
import pl.app.item.application.domain.Weapon;
import pl.app.trader.application.domain.Trader;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TraderDomainRepository {
    Mono<Trader> fetchByGodId(ObjectId godId);
}
