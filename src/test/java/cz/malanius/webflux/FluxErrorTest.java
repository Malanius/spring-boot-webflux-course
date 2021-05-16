package cz.malanius.webflux;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

class FluxErrorTest {

    @Test
    void errorHandling() {
        Flux<String> flux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception occurred.")))
                .concatWith(Flux.just("D"))
                .onErrorResume(e -> {
                    System.err.println(e);
                    return Flux.just("default", "default1");
                })
                .log();

        StepVerifier.create(flux)
                .expectSubscription()
                .expectNext("A", "B", "C")
//              .expectError(RuntimeException.class)
//              .verify();
                .expectNext("default", "default1")
                .verifyComplete();
    }

    @Test
    void errorHandlingWithOnErrorReturn() {
        Flux<String> flux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception occurred.")))
                .concatWith(Flux.just("D"))
                .onErrorReturn("default")
                .log();

        StepVerifier.create(flux)
                .expectSubscription()
                .expectNext("A", "B", "C")
                .expectNext("default")
                .verifyComplete();
    }

    @Test
    void errorHandlingWithOnErrorMap() {
        Flux<String> flux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception occurred.")))
                .concatWith(Flux.just("D"))
                .onErrorMap(CustomException::new)
                .log();

        StepVerifier.create(flux)
                .expectSubscription()
                .expectNext("A", "B", "C")
                .expectError(CustomException.class)
                .verify();
    }

    @Test
    void errorHandlingWithOnErrorMapWithRetry() {
        Flux<String> flux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception occurred.")))
                .concatWith(Flux.just("D"))
                .onErrorMap(CustomException::new)
                .retry(2) // will retry the whole Flux 2 times, totalling in 3 passes
                .log();

        StepVerifier.create(flux)
                .expectSubscription()
                .expectNext("A", "B", "C")
                .expectNext("A", "B", "C")
                .expectNext("A", "B", "C")
                .expectError(CustomException.class)
                .verify();
    }
}
