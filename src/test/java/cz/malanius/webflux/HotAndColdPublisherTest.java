package cz.malanius.webflux;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.time.Duration;

class HotAndColdPublisherTest {

    @Test
    void coldPublisherTest() throws InterruptedException {
        Flux<String> flux = Flux.just("A", "B", "C", "D", "E", "F")
                .delayElements(Duration.ofSeconds(1))
                .log();

        flux.subscribe(s -> System.out.println("Subscriber 1: " + s)); // emits values from beginning
        Thread.sleep(2_000);
        flux.subscribe(s -> System.out.println("Subscriber 2: " + s)); // emits values from beginning
        Thread.sleep(4_000);

    }
}
