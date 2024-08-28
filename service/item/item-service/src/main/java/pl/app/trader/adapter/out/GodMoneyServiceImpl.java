package pl.app.trader.adapter.out;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;
import pl.app.account.http.GodMoneyControllerHttpInterface;
import pl.app.common.shared.model.Money;
import pl.app.trader.application.port.out.GodMoneyService;
import reactor.core.publisher.Mono;

// TODO
@Component
@RequiredArgsConstructor
class GodMoneyServiceImpl implements GodMoneyService {
    private final GodMoneyControllerHttpInterface godMoneyControllerHttpInterface;

    @Override
    public Mono<Void> addMoney(ObjectId godId, Money money) {
        return Mono.empty();
    }

    @Override
    public Mono<Void> subtractMoney(ObjectId godId, Money money) {
        return Mono.empty();
    }
}
