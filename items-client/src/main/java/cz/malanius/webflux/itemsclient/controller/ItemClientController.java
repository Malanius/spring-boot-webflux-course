package cz.malanius.webflux.itemsclient.controller;

import cz.malanius.webflux.itemsclient.domain.Item;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/client")
public class ItemClientController {

    // Create client with base URL
    WebClient webClient = WebClient.create("http://localhost:8080");

    @GetMapping("/retrieve")
    public Flux<Item> getAllItemsUsingRetrieve() {
        return webClient.get().uri("/items")
                .retrieve()
                .bodyToFlux(Item.class)
                .log("Items retrieve");
    }

    @GetMapping("/exchange-deprec")
    public Flux<Item> getAllItemsUsingExchangeDeprec() {
        return webClient.get().uri("/items")
                .exchange()
                .flatMapMany(clientResponse -> clientResponse.bodyToFlux(Item.class))
                .log("Items exchange");
    }

    @GetMapping("/exchange")
    public Flux<Item> getAllItemsUsingExchangeToFlux() {
        return webClient.get().uri("/items")
                // Should handle various response code there
                .exchangeToFlux(clientResponse -> clientResponse.bodyToFlux(Item.class))
                .log("Items exchange");
    }

    @GetMapping("/retrieve/item/{id}")
    public Mono<Item> getOneItem(@PathVariable String id) {
        return webClient.get().uri("/items/{id}", id)
                .retrieve()
                .bodyToMono(Item.class)
                .log("Single item retrieve");
    }

    @PostMapping("/create-item")
    public Mono<Item> createItem(@RequestBody Item item) {
        return webClient.post().uri("/items")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(item), Item.class)
                .retrieve()
                .bodyToMono(Item.class)
                .log("Create item");
    }

    @PutMapping("/update-item/{id}")
    public Mono<Item> updateItem(@PathVariable String id, @RequestBody Item item) {
        return webClient.put().uri("/items/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(item), Item.class)
                .retrieve()
                .bodyToMono(Item.class)
                .log("Create item");
    }
}
