package meugeninua.masterdetails.controllers;

import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;

import java.util.concurrent.atomic.AtomicLong;

import static meugeninua.masterdetails.util.TestUtil.buildClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MasterCrudTests {

    @LocalServerPort
    private int port;

    private final AtomicLong masterId = new AtomicLong();

    @Test
    @Order(1)
    void whenMasterCreate_thenResponseCreated() {
        var body = """
            {
              "name": "Master 1",
              "details": [],
              "count": 1
            }
            """;

        buildClient(port).post()
            .uri("/masters")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isCreated()
            .expectBody()
            .jsonPath("$.id").isNumber()
            .jsonPath("$.id").value(Long.class, masterId::set);
    }

    @Test
    @Order(2)
    void whenMasterGet_thenResponseOk() {
        buildClient(port).get()
            .uri("/masters/{masterId}", masterId.get())
            .exchange()
            .expectStatus().isOk();
    }

    @Test
    @Order(3)
    void whenMasterUpdate_thenResponseOk() {
        var body = """
            {
              "name": "Master 1 (updated)",
              "details": [],
              "count": 2
            }
            """;

        buildClient(port).put()
            .uri("/masters/{masterId}", masterId.get())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isOk();
    }

    @Test
    @Order(4)
    void whenMasterGetAll_thenResponseOk() {
        buildClient(port).get()
            .uri("/masters")
            .exchange()
            .expectStatus().isOk();
    }

    @Test
    @Order(5)
    void whenMasterDelete_thenResponseNoContent() {
        buildClient(port).delete()
            .uri("/masters/{masterId}", masterId.get())
            .exchange()
            .expectStatus().isNoContent();
    }

    @Test
    @Order(6)
    void whenMasterDeleted_thenResponseNotFound() {
        buildClient(port).get()
            .uri("/masters/{masterId}", masterId.get())
            .exchange()
            .expectStatus().isNotFound();
    }
}
