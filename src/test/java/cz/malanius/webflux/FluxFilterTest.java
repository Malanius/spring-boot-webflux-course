package cz.malanius.webflux;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

class FluxFilterTest {

    List<String> namesList = Arrays.asList("adam", "anna", "jack", "jenny");

    @Test
    void filterTest() {
        Flux<String> namesFlux = Flux.fromIterable(namesList) // "adam", "anna", "jack", "jenny"
                .filter(s -> s.startsWith("a")) //"adam", "anna"
                .log();

        StepVerifier.create(namesFlux)
                .expectNext("adam", "anna")
                .verifyComplete();
    }

    @Test
    void filterTestLength() {
        Flux<String> namesFlux = Flux.fromIterable(namesList) // "adam", "anna", "jack", "jenny"
                .filter(s -> s.length() > 4) //"jenny"
                .log();

        StepVerifier.create(namesFlux)
                .expectNext("jenny")
                .verifyComplete();
    }
}
