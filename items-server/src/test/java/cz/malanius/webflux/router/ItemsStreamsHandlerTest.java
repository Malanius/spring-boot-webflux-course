package cz.malanius.webflux.router;

import cz.malanius.webflux.document.ItemCapped;
import cz.malanius.webflux.repository.ItemReactiveCappedRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureWebTestClient
public class ItemsStreamsHandlerTest {

    private final ItemReactiveCappedRepository repository;
    private final ReactiveMongoOperations mongoOperations;
    private final WebTestClient webTestClient;

    @Autowired
    public ItemsStreamsHandlerTest(ItemReactiveCappedRepository repository,
                                   ReactiveMongoOperations mongoOperations,
                                   WebTestClient webTestClient) {
        this.repository = repository;
        this.mongoOperations = mongoOperations;
        this.webTestClient = webTestClient;
    }

    @BeforeEach
    void setUp() {
        mongoOperations.dropCollection(ItemCapped.class)
                .then(mongoOperations.createCollection(ItemCapped.class, CollectionOptions.empty()
                        .maxDocuments(20).size(5_000).capped()))
                .block();

        Flux<ItemCapped> itemCappedFlux = Flux.interval(Duration.ofMillis(100))
                .map(i -> new ItemCapped(null, "Random item " + i, 100.0 + i))
                .take(5)
                .log();

        repository.insert(itemCappedFlux)
                .doOnNext(itemCapped -> log.info("Inserted: {}", itemCapped))
                .blockLast();
    }

    @Test
    void itemsStream() {
        Flux<ItemCapped> itemFlux = webTestClient.get().uri("/fn/items/stream")
                .exchange()
                .expectStatus().isOk()
                .returnResult(ItemCapped.class)
                .getResponseBody()
                .take(5);

        StepVerifier.create(itemFlux)
                .expectNextCount(5)
                .thenCancel()
                .verify();
    }
}
