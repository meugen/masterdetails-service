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
class DetailCrudTests {

    @LocalServerPort
    private int port;

    private final AtomicLong masterId = new AtomicLong();
    private final AtomicLong detailId = new AtomicLong();

    @BeforeAll
    void createMaster() {
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
            .exchangeSuccessfully()
            .expectBody()
            .jsonPath("$.id").value(Long.class, masterId::set);
    }

    @AfterAll
    void deleteMaster() {
        buildClient(port).delete()
            .uri("/masters/{masterId}", masterId.get())
            .exchangeSuccessfully();
    }

    @Test
    @Order(1)
    void whenDetailCreate_thenResponseCreated() {
        var body = """
            {
              "name": "Detail 1"
            }
            """;

        buildClient(port).post()
            .uri("/masters/{masterId}/details", masterId.get())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isCreated()
            .expectBody()
            .jsonPath("$.id").isNumber()
            .jsonPath("$.id").value(Long.class, detailId::set);
    }

    @Test
    @Order(2)
    void whenDetailGet_thenResponseOk() {
        buildClient(port).get()
            .uri("/masters/{masterId}/details/{detailId}", masterId.get(), detailId.get())
            .exchange()
            .expectStatus().isOk();
    }

    @Test
    @Order(3)
    void whenDetailUpdate_thenResponseOk() {
        var body = """
            {
              "name": "Detail 1 (updated)"
            }
            """;

        buildClient(port).put()
            .uri("/masters/{masterId}/details/{detailId}", masterId.get(), detailId.get())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isOk();
    }

    @Test
    @Order(4)
    void whenDetailGetAll_thenResponseOk() {
        buildClient(port).get()
            .uri("/masters/{masterId}/details", masterId.get())
            .exchange()
            .expectStatus().isOk();
    }

    @Test
    @Order(5)
    void whenDetailDelete_thenResponseNoContent() {
        buildClient(port).delete()
            .uri("/masters/{masterId}/details/{detailId}", masterId.get(), detailId.get())
            .exchange()
            .expectStatus().isNoContent();
    }

    @Test
    @Order(6)
    void whenDetailDeleted_thenResponseNotFound() {
        buildClient(port).get()
            .uri("/masters/{masterId}/details/{detailId}", masterId.get(), detailId.get())
            .exchange()
            .expectStatus().isNotFound();
    }
}
