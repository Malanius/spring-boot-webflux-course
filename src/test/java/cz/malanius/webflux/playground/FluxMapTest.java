package cz.malanius.webflux.playground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

class FluxMapTest {

    List<String> namesList = Arrays.asList("adam", "anna", "jack", "jenny");

    @Test
    void transformUsingMap() {
        Flux<String> namesFlux = Flux.fromIterable(namesList) // "adam", "anna", "jack", "jenny"
                .map(String::toUpperCase) // "ADAM", "ANNA", "JACK", "JENNY"
                .log();

        StepVerifier.create(namesFlux)
                .expectNext("ADAM", "ANNA", "JACK", "JENNY")
                .verifyComplete();
    }

    @Test
    void transformUsingMapToLength() {
        Flux<Integer> namesFlux = Flux.fromIterable(namesList) // "adam", "anna", "jack", "jenny"
                .map(String::length) // 4, 4, 4, 5
                .log();

        StepVerifier.create(namesFlux)
                .expectNext(4, 4, 4, 5)
                .verifyComplete();
    }

    @Test
    void transformUsingMapToLengthRepeat() {
        Flux<Integer> namesFlux = Flux.fromIterable(namesList) // "adam", "anna", "jack", "jenny"
                .map(String::length) // 4, 4, 4, 5
                .repeat(1) // repeats previous flux N times
                .log();

        StepVerifier.create(namesFlux)
                .expectNext(4, 4, 4, 5)
                .expectNext(4, 4, 4, 5)
                .verifyComplete();
    }

    @Test
    void transformUsingMapFilter() {
        Flux<String> namesFlux = Flux.fromIterable(namesList) // "adam", "anna", "jack", "jenny"
                .filter(s -> s.length() > 4) // "jenny"
                .map(String::toUpperCase) // "JENNY"
                .log();

        StepVerifier.create(namesFlux)
                .expectNext("JENNY")
                .verifyComplete();
    }
}
