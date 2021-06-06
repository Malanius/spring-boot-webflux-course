package cz.malanius.webflux.router;

import cz.malanius.webflux.document.Item;
import cz.malanius.webflux.repository.ItemReactiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class ItemsHandler {

    private final ItemReactiveRepository itemsRepository;

    @Autowired
    public ItemsHandler(ItemReactiveRepository itemsRepository) {
        this.itemsRepository = itemsRepository;
    }

    public Mono<ServerResponse> getAllItems(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(itemsRepository.findAll(), Item.class);
    }
}
