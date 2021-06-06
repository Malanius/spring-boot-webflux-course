package cz.malanius.webflux.controller;

import cz.malanius.webflux.document.ItemCapped;
import cz.malanius.webflux.repository.ItemReactiveCappedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/items/stream")
public class ItemStreamController {

    private final ItemReactiveCappedRepository repository;

    @Autowired
    public ItemStreamController(ItemReactiveCappedRepository repository) {
        this.repository = repository;
    }

    @GetMapping(produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<ItemCapped> streamItems() {
        return repository.findItemsBy();
    }
}
