package cz.malanius.webflux.repository;

import cz.malanius.webflux.document.Item;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface ItemReactiveRepository extends ReactiveMongoRepository<Item, String> {

    Flux<Item> findAllByDescription(String description);
    
}
