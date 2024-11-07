package pl.app.attack.army_walk.domain.application;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

public class TestA {

    @Test
    void test() {
        Mono<?> doingSomething = doSomething()
                .doOnSubscribe(subscription -> System.out.println("doOnSubscribe"))
                .doOnSuccess(integer -> System.out.println("doOnSuccess"))
                .doOnError(throwable -> System.out.println("doOnError"));

        doingSomething.block();
    }

    static Mono<Integer> doSomething() {
        return Mono.fromSupplier(() -> {
            System.out.println("doing something");
            return 123;
        });
    }
}
