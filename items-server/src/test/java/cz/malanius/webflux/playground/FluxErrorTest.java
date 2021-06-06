package cz.malanius.webflux.playground;

import cz.malanius.webflux.CustomException;
import org.junit.jupiter.api.Test;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.util.retry.Retry;

import java.time.Duration;

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

    @Test
    void errorHandlingWithOnErrorMapWithRetryWhen() {
        // Defines how to handle retries on errors
        Retry retrySpec = Retry.fixedDelay(2, Duration.ofSeconds(1)) // retries two times, after 1 s
                .filter(e -> e instanceof CustomException) // sets which errors are retried
                .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> Exceptions.propagate(retrySignal.failure()));

        Flux<String> flux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception occurred.")))
                .concatWith(Flux.just("D"))
                .onErrorMap(CustomException::new)
                .retryWhen(retrySpec)
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
