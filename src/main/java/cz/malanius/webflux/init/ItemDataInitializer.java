package cz.malanius.webflux.init;

import cz.malanius.webflux.document.Item;
import cz.malanius.webflux.repository.ItemReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class ItemDataInitializer implements CommandLineRunner {

    private final ItemReactiveRepository itemRepository;
    private final List<Item> items = Arrays.asList(
            new Item(null, "Something", 500.5),
            new Item(null, "Anything", 20.0),
            new Item(null, "Everything", 999.9),
            new Item("ABC", "Universe", Double.MAX_VALUE)
    );

    @Autowired
    public ItemDataInitializer(ItemReactiveRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        initData();
    }

    private void initData() {
        itemRepository.deleteAll()
                .thenMany(Flux.fromIterable(items))
                .flatMap(itemRepository::save)
                .thenMany(itemRepository.findAll())
                .subscribe(item -> log.info("Item inserted: {}", item));
    }
}
