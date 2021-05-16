package cz.malanius.webflux;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

import java.time.Duration;

class FluxCombineTest {

    @Test
    void combineUsingMerge() {
        Flux<String> flux1 = Flux.just("A", "B", "C");
        Flux<String> flux2 = Flux.just("D", "E", "F");

        Flux<String> merged = Flux.merge(flux1, flux2).log();

        StepVerifier.create(merged)
                .expectSubscription()
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }

    @Test
    void combineUsingMergeWithDelay() {
        Flux<String> flux1 = Flux.just("A", "B", "C").delayElements(Duration.ofSeconds(1));
        Flux<String> flux2 = Flux.just("D", "E", "F").delayElements(Duration.ofSeconds(1));

        Flux<String> merged = Flux.merge(flux1, flux2).log();

        StepVerifier.create(merged)
                .expectSubscription()
//                .expectNext("A", "B", "C", "D", "E", "F") // will fails as order is not preserved
                .expectNextCount(6)
                .verifyComplete();
    }

    @Test
    void combineUsingConcat() {
        Flux<String> flux1 = Flux.just("A", "B", "C");
        Flux<String> flux2 = Flux.just("D", "E", "F");

        Flux<String> merged = Flux.concat(flux1, flux2).log();

        StepVerifier.create(merged)
                .expectSubscription()
                .expectNext("A", "B", "C", "D", "E", "F") // will fails as order is not preserved
                .verifyComplete();
    }

    @Test
    void combineUsingConcatWithDelay() {
        VirtualTimeScheduler.getOrSet();
        Flux<String> flux1 = Flux.just("A", "B", "C").delayElements(Duration.ofSeconds(1));
        Flux<String> flux2 = Flux.just("D", "E", "F").delayElements(Duration.ofSeconds(1));

        Flux<String> merged = Flux.concat(flux1, flux2).log();

//        StepVerifier.create(merged)
//                .expectSubscription()
//                .expectNext("A", "B", "C", "D", "E", "F") // will not fail as it maintains order, but waits
//                .verifyComplete();

        // This shortens the test from 6s ~200ms to ~200 ms
        StepVerifier.withVirtualTime(() -> merged)
                .expectSubscription()
                .thenAwait(Duration.ofSeconds(6))
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();

    }

    @Test
    void combineUsingZip() {
        Flux<String> flux1 = Flux.just("A", "B", "C");
        Flux<String> flux2 = Flux.just("D", "E", "F");

        Flux<String> merged = Flux.zip(flux1, flux2, String::concat).log(); //A,D : B,E : C,F

        StepVerifier.create(merged)
                .expectSubscription()
                .expectNext("AD", "BE", "CF") // will fails as order is not preserved
                .verifyComplete();
    }
}
