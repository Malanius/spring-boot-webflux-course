package cz.malanius.webflux;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

class FluxTest {

    @Test
    void fluxTest() {
        // Creating a flux
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
                .concatWith(Flux.error(new RuntimeException("Exception occurred.")))
                .concatWith(Flux.just("After error")) // This won't be emitted after error is encountered
                .log();
        // Only on when subscribing flux emits the data
        stringFlux.subscribe(System.out::println,
                System.err::println, // Second parameter for handling exceptions
                () -> System.out.println("Completed.") // Function to call after `onComplete()`
        );
    }

    @Test
    void fluxTestElement() {
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
                .log();

        StepVerifier.create(stringFlux)
                .expectNext("Spring")
                .expectNext("Spring Boot")
                .expectNext("Reactive Spring")
                .verifyComplete();
    }

    @Test
    void fluxTestElementWithError() {
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
                .concatWith(Flux.error(new RuntimeException("Exception occurred.")))
                .log();

        StepVerifier.create(stringFlux)
                .expectNext("Spring")
                .expectNext("Spring Boot")
                .expectNext("Reactive Spring")
//                .expectError(RuntimeException.class)
                .expectErrorMessage("Exception occurred.")
                .verify();
    }

    @Test
    void fluxTestElementsCount() {
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
                .log();

        StepVerifier.create(stringFlux)
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void fluxTestNextElementsWithVarargs() {
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
                .concatWith(Flux.error(new RuntimeException("Exception occurred.")))
                .log();

        StepVerifier.create(stringFlux)
                .expectNext("Spring", "Spring Boot", "Reactive Spring")
                .expectError(RuntimeException.class)
//                .expectErrorMessage("Exception occurred.")
                .verify();
    }

}
