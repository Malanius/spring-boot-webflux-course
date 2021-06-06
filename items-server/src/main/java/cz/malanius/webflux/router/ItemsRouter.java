package cz.malanius.webflux.router;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class ItemsRouter {

    @Bean
    public RouterFunction<ServerResponse> itemsRoute(ItemsHandler itemsHandler) {
        return RouterFunctions
                .route(GET("/fn/items").and(accept(MediaType.APPLICATION_JSON)), itemsHandler::getAllItems)
                .andRoute(GET("/fn/items/{id}").and(accept(MediaType.APPLICATION_JSON)), itemsHandler::getOneItem)
                .andRoute(POST("/fn/items").and(accept(MediaType.APPLICATION_JSON)), itemsHandler::createItem)
                .andRoute(PUT("/fn/items/{id}").and(accept(MediaType.APPLICATION_JSON)), itemsHandler::updateItem)
                .andRoute(DELETE("/fn/items/{id}"), itemsHandler::deleteItem);
    }

    @Bean
    public RouterFunction<ServerResponse> errorRoute(ItemsHandler itemsHandler){
        return RouterFunctions
                .route(GET("/fn/runtime-exception").and(accept(MediaType.APPLICATION_JSON)), itemsHandler::exception);
    }
}
