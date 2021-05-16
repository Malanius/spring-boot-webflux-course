package cz.malanius.webflux;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Optional;

class MonoFactoryTest {

    @Test
    void monoJust() {
        Mono<String> mono = Mono.just("Data");

        StepVerifier.create(mono)
                .expectNext("Data")
                .verifyComplete();
    }

    @Test
    void monoJustOrEmpty() {
//      Mono<String> mono = Mono.justOrEmpty(null);            // Same as Mono.empty()
        Mono<String> mono = Mono.justOrEmpty(Optional.empty());// Same as Mono.empty()

        // As mono is empty, there is no data to trigger onNext()
        StepVerifier.create(mono)
                .verifyComplete();
    }
}
