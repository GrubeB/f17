package pl.app.trader.application.port.out;

import org.bson.types.ObjectId;
import pl.app.common.shared.model.Money;
import reactor.core.publisher.Mono;

public interface GodMoneyService {
    Mono<Void> addMoney(ObjectId godId, Money money);

    Mono<Void> subtractMoney(ObjectId godId, Money money);
}
