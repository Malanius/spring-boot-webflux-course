package cz.malanius.webflux;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

class FluxFactoryTest {

    String[] namesArray = new String[]{"adam", "anna", "jack", "jenny"};
    List<String> namesList = Arrays.asList(namesArray);

    @Test
    void fluxFromIterable() {
        Flux<String> namesFlux = Flux.fromIterable(namesList);

        StepVerifier.create(namesFlux.log())
                .expectNext("adam", "anna", "jack", "jenny")
                .verifyComplete();
    }

    @Test
    void fluxFromArray() {
        Flux<String> namesFlux = Flux.fromArray(namesArray);

        StepVerifier.create(namesFlux.log())
                .expectNext("adam", "anna", "jack", "jenny")
                .verifyComplete();
    }

    @Test
    void fluxFromStream() {
        Flux<String> namesFlux = Flux.fromStream(namesList.stream());

        StepVerifier.create(namesFlux.log())
                .expectNext("adam", "anna", "jack", "jenny")
                .verifyComplete();
    }
}
