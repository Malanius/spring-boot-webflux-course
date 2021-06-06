package cz.malanius.webflux.playground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

class FluxFlatMapTest {

    List<String> list = Arrays.asList("A", "B", "C", "D", "E", "F");

    @Test
    void transformUsingFlatMap() {
        Flux<String> flux = Flux.fromIterable(list) // "A", "B", "C", "D", "E", "F"
                .flatMap(s -> Flux.fromIterable(convertToList(s))) // DB or external call that returns a flux -> s -> Flux<String>
                .log();

        StepVerifier.create(flux)
                .expectNextCount(list.size() * 2L) // we expect doubled size from convertToList()
                .verifyComplete();
    }

    @Test
    void transformUsingFlatMapParallel() {
        Flux<String> flux = Flux.fromIterable(list) // "A", "B", "C", "D", "E", "F"
                .window(2) // waits for 2 elements, Flux<Flux<String>>, (A,B), (C,D), (E,F)
                .flatMap(f -> f.map(this::convertToList).subscribeOn(Schedulers.parallel())) // Flux<List<String>>
                .flatMap(Flux::fromIterable)
                .log();

        StepVerifier.create(flux)
                .expectNextCount(list.size() * 2L) // we expect doubled size from convertToListFunction
                .verifyComplete();
    }

    @Test
    void transformUsingFlatMapParallelWithOrder() {
        Flux<String> flux = Flux.fromIterable(list) // "A", "B", "C", "D", "E", "F"
                .window(2) // waits for 2 elements, Flux<Flux<String>>, (A,B), (C,D), (E,F)
//                .concatMap(f -> f.map(this::convertToList).subscribeOn(Schedulers.parallel())) // Flux<List<String>>, same as flatMap() but maitans order
                .flatMapSequential(f -> f.map(this::convertToList).subscribeOn(Schedulers.parallel())) // Flux<List<String>>, same as flatMap() but maitans order
                .flatMap(Flux::fromIterable)
                .log();

        StepVerifier.create(flux)
                .expectNextCount(list.size() * 2L) // we expect doubled size from convertToListFunction
                .verifyComplete();
    }

    private List<String> convertToList(String s) {
        try {
            Thread.sleep(1_000); //simulate long operation
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Arrays.asList(s, "otherValue");
    }


}
