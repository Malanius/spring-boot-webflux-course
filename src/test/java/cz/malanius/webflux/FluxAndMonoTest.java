package cz.malanius.webflux;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

class FluxAndMonoTest {

    @Test
    void fluxTest() {
        // Creating a flux
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring");
        // Only on when subscribing flux emits the data
        stringFlux.subscribe(System.out::println);
    }

}
