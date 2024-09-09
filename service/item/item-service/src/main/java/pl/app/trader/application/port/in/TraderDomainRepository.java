package pl.app.trader.application.port.in;

import org.bson.types.ObjectId;
import pl.app.trader.application.domain.Trader;
import reactor.core.publisher.Mono;

public interface TraderDomainRepository {
    Mono<Trader> fetchByGodId(ObjectId godId);
}
