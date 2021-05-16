package cz.malanius.webflux;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

class FluxAndMonoTest {

    @Test
    void fluxTest() {
        // Creating a flux
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
                .concatWith(Flux.error(new RuntimeException("Exception occurred.")))
                .concatWith(Flux.just("After error")) // This won't be emmitet after error is encountered
                .log();
        // Only on when subscribing flux emits the data
        stringFlux.subscribe(System.out::println, System.err::println); // Second parameter for handling exceptions
    }

}
