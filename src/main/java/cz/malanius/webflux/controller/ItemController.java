package cz.malanius.webflux.controller;

import cz.malanius.webflux.document.Item;
import cz.malanius.webflux.repository.ItemReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemReactiveRepository repository;

    @Autowired
    public ItemController(ItemReactiveRepository repository) {
        this.repository = repository;
    }

    @GetMapping()
    public Flux<Item> getAllItems() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Item>> getItem(@PathVariable String id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Item> createItem(@RequestBody Item item) {
        return repository.save(item);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Item>> updateItem(@PathVariable String id, @RequestBody Item item) {
        return repository.findById(id)
                .flatMap(currentItem -> {
                    Item updatedItem = currentItem.toBuilder()
                            .price(item.getPrice())
                            .description(item.getDescription())
                            .build();
                    return repository.save(updatedItem);
                })
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteItem(@PathVariable String id) {
        return repository.deleteById(id);
    }
}
