package cz.malanius.webflux.playground;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@WebFluxTest
class FluxAndMonoControllerTest {

    private final WebTestClient testClient;

    @Autowired
    FluxAndMonoControllerTest(WebTestClient testClient) {
        this.testClient = testClient;
    }

    @Test
    void getMono() {
        Integer expectedValue = 1;
        testClient.get().uri("/mono")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Integer.class)
                .consumeWith(response -> assertEquals(expectedValue, response.getResponseBody()));
    }

    @Test
    void getFluxApproach1() {
        Flux<Integer> flux = testClient.get().uri("/flux")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .returnResult(Integer.class)
                .getResponseBody();

        StepVerifier.create(flux)
                .expectSubscription()
                .expectNext(1, 2, 3, 4)
                .verifyComplete();
    }

    @Test
    void getFluxApproach2() {
        testClient.get().uri("/flux")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Integer.class)
                .hasSize(4);
    }

    @Test
    void getFluxApproach3() {
        List<Integer> expected = Arrays.asList(1, 2, 3, 4);

        EntityExchangeResult<List<Integer>> exchangeResult = testClient.get().uri("/flux")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Integer.class)
                .returnResult();

        assertEquals(expected, exchangeResult.getResponseBody());
    }

    @Test
    void getFluxApproach4() {
        List<Integer> expected = Arrays.asList(1, 2, 3, 4);

        testClient.get().uri("/flux")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Integer.class)
                .consumeWith(response -> {
                    assertEquals(expected, response.getResponseBody());
                });

    }

    @Test
    void getFluxStream() {
        Flux<Long> flux = testClient.get().uri("/flux-stream")
                .accept(MediaType.APPLICATION_NDJSON)
                .exchange()
                .expectStatus().isOk()
                .returnResult(Long.class)
                .getResponseBody();

        StepVerifier.create(flux)
                .expectSubscription()
                .expectNext(0L, 1L, 2L)
                .thenCancel()
                .verify();
    }
}
