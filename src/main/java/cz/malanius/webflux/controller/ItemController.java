package cz.malanius.webflux.controller;

import cz.malanius.webflux.document.Item;
import cz.malanius.webflux.repository.ItemReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@Slf4j
@RestController
@RequestMapping("items")
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
}
