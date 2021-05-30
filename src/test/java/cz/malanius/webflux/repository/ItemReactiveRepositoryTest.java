package cz.malanius.webflux.repository;

import cz.malanius.webflux.document.Item;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

@ActiveProfiles("test")
@DataMongoTest
class ItemReactiveRepositoryTest {

    private final ItemReactiveRepository repository;
    private final List<Item> items = Arrays.asList(
            new Item(null, "Something", 500.5),
            new Item(null, "Anything", 20.0),
            new Item(null, "Everything", 999.9),
            new Item("ABC", "Universe", Double.MAX_VALUE)
    );

    @Autowired
    ItemReactiveRepositoryTest(ItemReactiveRepository repository) {
        this.repository = repository;
    }

    @BeforeEach
    void setUp() {
        repository.insert(Flux.fromIterable(items))
                .doOnNext(item -> System.out.println("Inserted item: " + item))
                .blockLast(); // Waits for completion of all preceding call
    }

    @AfterEach
    void tearDown() {
        repository.deleteAll().block();
    }

    @Test
    void getAllItems() {
        StepVerifier.create(repository.findAll())
                .expectSubscription()
                .expectNextCount(items.size())
                .verifyComplete();
    }

    @Test
    void getItemById() {
        StepVerifier.create(repository.findById("ABC"))
                .expectSubscription()
                .expectNextMatches(item -> item.getDescription().equals("Universe"))
                .verifyComplete();
    }

    @Test
    void getItemByDescription() {
        StepVerifier.create(repository.findAllByDescription("Universe").log("findAllByDescription: "))
                .expectSubscription()
                .expectNextCount(1)
                .verifyComplete();
    }
}
