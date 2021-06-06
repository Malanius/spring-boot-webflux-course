package cz.malanius.webflux.playground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

class FluxWithTimeTest {

    @Test
    void infiniteSequence() throws InterruptedException {
        Flux<Long> flux = Flux.interval(Duration.ofMillis(200))// Starts from 0 -> ...
                .log();

        flux.subscribe(System.out::println);

        Thread.sleep(3_000);
    }

    @Test
    void infiniteSequenceTest() {
        Flux<Long> flux = Flux.interval(Duration.ofMillis(200))// Starts from 0 -> ...
                .take(3)
                .log();

        StepVerifier.create(flux)
                .expectSubscription()
                .expectNext(0L, 1L, 2L)
                .verifyComplete();
    }

    @Test
    void infiniteSequenceMap() {
        Flux<Integer> flux = Flux.interval(Duration.ofMillis(200))// Starts from 0 -> ...
                .map(Long::intValue)
                .take(3)
                .log();

        StepVerifier.create(flux)
                .expectSubscription()
                .expectNext(0, 1, 2)
                .verifyComplete();
    }

    @Test
    void infiniteSequenceMapWithDelay() {
        Flux<Integer> flux = Flux.interval(Duration.ofMillis(200))// Starts from 0 -> ...
                .delayElements(Duration.ofSeconds(1))
                .map(Long::intValue)
                .take(3)
                .log();

        StepVerifier.create(flux)
                .expectSubscription()
                .expectNext(0, 1, 2)
                .verifyComplete();
    }
}
