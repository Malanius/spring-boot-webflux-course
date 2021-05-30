package cz.malanius.webflux.repository;

import cz.malanius.webflux.document.Item;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import java.util.UUID;

public interface ItemReactiveRepository extends ReactiveMongoRepository<Item, UUID> {
}
