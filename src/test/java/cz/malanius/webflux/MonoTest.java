package cz.malanius.webflux;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class MonoTest {

    @Test
    void monoTest() {
        Mono<String> stringMono = Mono.just("Spring"); //Mono can hold only one value

        StepVerifier.create(stringMono.log())
                .expectNext("Spring")
                .verifyComplete();
    }

    @Test
    void monoTestWithError() {
        StepVerifier.create(Mono.error(new RuntimeException("Exception occurred.")).log())
                .expectError(RuntimeException.class)
                .verify();
    }
}
