package cz.malanius.webflux.playground;

import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

class FluxBackpressureTest {

    @Test
    void backpressureTest() {
        Flux<Integer> flux = Flux.range(1, 10)
                .log();

        StepVerifier.create(flux)
                .expectSubscription()
                .thenRequest(1)
                .expectNext(1)
                .thenRequest(2)
                .expectNext(2, 3)
                .thenCancel()
                .verify();
    }

    @Test
    void backpressure() {
        Flux<Integer> flux = Flux.range(1, 10)
                .log();

        flux.subscribe(System.out::println,
                System.err::println,
                () -> System.out.println("Done"), // not printed as this is called on onComplete()
                subscription -> subscription.request(2)
        );
    }

    @Test
    void backpressureCancel() {
        Flux<Integer> flux = Flux.range(1, 10)
                .log();

        flux.subscribe(System.out::println,
                System.err::println,
                () -> System.out.println("Done"), // not printed as this is called on onComplete()
                Subscription::cancel
        );
    }

    @Test
    void customizedBackpressure() {
        Flux<Integer> flux = Flux.range(1, 10)
                .log();

        flux.subscribe(new BaseSubscriber<Integer>() {
            @Override
            protected void hookOnNext(Integer value) {
                request(1);
                System.out.println(value);
                if (value == 4) cancel();
            }
        });
    }
}
