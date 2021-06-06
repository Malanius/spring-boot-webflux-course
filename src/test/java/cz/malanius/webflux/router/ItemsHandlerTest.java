package cz.malanius.webflux.router;

import cz.malanius.webflux.document.Item;
import cz.malanius.webflux.repository.ItemReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureWebTestClient
class ItemsHandlerTest {

    private final WebTestClient testClient;
    private final ItemReactiveRepository repository;

    private final List<Item> items = Arrays.asList(
            new Item(null, "Something", 500.5),
            new Item(null, "Anything", 20.0),
            new Item(null, "Everything", 999.9),
            new Item("ABC", "Universe", Double.MAX_VALUE)
    );

    @Autowired
    ItemsHandlerTest(WebTestClient testClient, ItemReactiveRepository repository) {
        this.testClient = testClient;
        this.repository = repository;
    }

    @BeforeEach
    void setUp() {
        repository.saveAll(Flux.fromIterable(items))
                .doOnNext(item -> log.info("Inserted item: {}", item))
                .blockLast(); // Waits for completion of all preceding call
    }

    @AfterEach
    void tearDown() {
        repository.deleteAll().block();
    }

    @Test
    void getAllItems() {
        testClient.get().uri("/fn//items")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Item.class)
                .hasSize(items.size());
    }

    @Test
    void getAllItems2() {
        testClient.get().uri("/fn/items")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Item.class)
                .hasSize(items.size())
                .consumeWith(response -> {
                    response.getResponseBody()
                            .forEach(item -> assertNotNull(item.getId()));

                });
    }

    @Test
    void getAllItems3() {
        Flux<Item> itemFlux = testClient.get().uri("/fn/items")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .returnResult(Item.class)
                .getResponseBody();

        StepVerifier.create(itemFlux.log("Test 3"))
                .expectSubscription()
                .expectNextCount(4)
                .verifyComplete();
    }

    @Test
    void getOneItem() {
        testClient.get().uri("/fn/items/{id}", "ABC")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.description", "Universe");
    }

    @Test
    void getNotFoundItem() {
        testClient.get().uri("/fn/items/{id}", "XYZ")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void createItem() {
        Item item = new Item(null, "God powers", 777.77);
        testClient.post().uri("/fn/items")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(item), Item.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.description").isEqualTo("God powers")
                .jsonPath("$.price").isEqualTo(777.77);
    }

    @Test
    void updateItem() {
        double newPrice = 666.66;
        Item item = new Item(null, "Universe", newPrice);
        testClient.put().uri("/fn/items/{id}", "ABC")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(item), Item.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.description").isEqualTo("Universe")
                .jsonPath("$.price").isEqualTo(newPrice);
    }

    @Test
    void updateNonExistentItem() {
        double newPrice = 666.66;
        Item item = new Item(null, "Universe", newPrice);
        testClient.put().uri("/fn/items/{id}", "XYZ")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(item), Item.class)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void deleteItem() {
        testClient.delete().uri("/fn/items/{id}", "ABC")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Void.class);
    }
}
