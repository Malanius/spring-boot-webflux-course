package cz.malanius.webflux.init;

import cz.malanius.webflux.document.Item;
import cz.malanius.webflux.document.ItemCapped;
import cz.malanius.webflux.repository.ItemReactiveCappedRepository;
import cz.malanius.webflux.repository.ItemReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@Profile("!test")
public class ItemDataInitializer implements CommandLineRunner {

    private final ReactiveMongoOperations mongoOperations;
    private final ItemReactiveRepository itemRepository;
    private final ItemReactiveCappedRepository itemCappedRepository;
    private final List<Item> items = Arrays.asList(
            new Item(null, "Something", 500.5),
            new Item(null, "Anything", 20.0),
            new Item(null, "Everything", 999.9),
            new Item("ABC", "Universe", Double.MAX_VALUE)
    );

    @Autowired
    public ItemDataInitializer(ReactiveMongoOperations mongoOperations,
                               ItemReactiveRepository itemRepository,
                               ItemReactiveCappedRepository itemCappedRepository) {
        this.mongoOperations = mongoOperations;
        this.itemRepository = itemRepository;
        this.itemCappedRepository = itemCappedRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        initData();
        createCappedCollection();
        initCappedData();
    }

    private void initData() {
        itemRepository.deleteAll()
                .thenMany(Flux.fromIterable(items))
                .flatMap(itemRepository::save)
                .thenMany(itemRepository.findAll())
                .subscribe(item -> log.info("Item inserted: {}", item));
    }

    private void createCappedCollection() {
        mongoOperations.dropCollection(ItemCapped.class)
                .then(mongoOperations.createCollection(ItemCapped.class, CollectionOptions.empty().
                        maxDocuments(20)
                        .size(5_000)
                        .capped()))
                .block();
    }

    private void initCappedData() {
        Flux<ItemCapped> itemCappedFlux = Flux.interval(Duration.ofSeconds(1))
                .map(i -> new ItemCapped(null, "Random item " + i, 100.0 + i))
                .log();

        itemCappedRepository.insert(itemCappedFlux)
                .subscribe(itemCapped -> log.info("Inserted: {}", itemCapped));

    }
}
