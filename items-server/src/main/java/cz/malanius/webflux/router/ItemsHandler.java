package cz.malanius.webflux.router;

import cz.malanius.webflux.document.Item;
import cz.malanius.webflux.document.ItemCapped;
import cz.malanius.webflux.repository.ItemReactiveCappedRepository;
import cz.malanius.webflux.repository.ItemReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class ItemsHandler {

    private final ItemReactiveRepository itemsRepository;
    private final ItemReactiveCappedRepository itemCappedRepository;

    @Autowired
    public ItemsHandler(ItemReactiveRepository itemsRepository, ItemReactiveCappedRepository itemCappedRepository) {
        this.itemsRepository = itemsRepository;
        this.itemCappedRepository = itemCappedRepository;
    }

    public Mono<ServerResponse> getAllItems(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(itemsRepository.findAll(), Item.class);
    }

    public Mono<ServerResponse> getOneItem(ServerRequest request) {
        String id = request.pathVariable("id");
        Mono<Item> itemMono = itemsRepository.findById(id);
        return itemMono.flatMap(item -> ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(item)))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> createItem(ServerRequest request) {
        Mono<Item> newItem = request.bodyToMono(Item.class);
        return newItem.flatMap(item -> ServerResponse.created(null)
                .contentType(MediaType.APPLICATION_JSON)
                .body(itemsRepository.save(item), Item.class));
    }

    public Mono<ServerResponse> deleteItem(ServerRequest request) {
        String id = request.pathVariable("id");
        Mono<Void> deletedItem = itemsRepository.deleteById(id);
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(deletedItem, Void.class);
    }

    public Mono<ServerResponse> updateItem(ServerRequest request) {
        String id = request.pathVariable("id");
        Mono<Item> updatedItem = request.bodyToMono(Item.class)
                .flatMap(item -> itemsRepository.findById(id)
                        .flatMap(currentItem -> {
                            Item updated = currentItem.toBuilder()
                                    .description(item.getDescription())
                                    .price(item.getPrice())
                                    .build();
                            return itemsRepository.save(updated);
                        }));

        return updatedItem.flatMap(item -> ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(item)))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> exception(ServerRequest request) {
        throw new RuntimeException("Exception occurred!");
    }

    public Mono<ServerResponse> itemsStream(ServerRequest request) {
        log.info("Handling streaming request: {}", request);
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_NDJSON)
                .body(itemCappedRepository.findItemsBy(), ItemCapped.class);
    }
}
