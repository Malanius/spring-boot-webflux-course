package cz.malanius.webflux;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Optional;
import java.util.function.Supplier;

class MonoFactoryTest {

    @Test
    void monoJust() {
        Mono<String> mono = Mono.just("Data");

        StepVerifier.create(mono.log())
                .expectNext("Data")
                .verifyComplete();
    }

    @Test
    void monoJustOrEmpty() {
//      Mono<String> mono = Mono.justOrEmpty(null);            // Same as Mono.empty()
        Mono<String> mono = Mono.justOrEmpty(Optional.empty());// Same as Mono.empty()

        // As mono is empty, there is no data to trigger onNext()
        StepVerifier.create(mono.log())
                .verifyComplete();
    }

    @Test
    void monoFromSupplier() {
        Supplier<String> supplier = () -> "Data";
        Mono<String> mono = Mono.fromSupplier(supplier);

        StepVerifier.create(mono.log())
                .expectNext("Data")
                .verifyComplete();
    }
}
