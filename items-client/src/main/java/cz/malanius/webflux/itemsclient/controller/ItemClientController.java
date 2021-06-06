package cz.malanius.webflux.itemsclient.controller;

import cz.malanius.webflux.itemsclient.domain.Item;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

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
}
