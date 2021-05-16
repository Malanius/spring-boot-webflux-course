package cz.malanius.webflux;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

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
}
