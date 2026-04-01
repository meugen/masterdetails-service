package meugeninua.masterdetails.controllers;

import meugeninua.masterdetails.configs.NoOpCachingConfiguration;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.concurrent.atomic.AtomicLong;

import static meugeninua.masterdetails.util.TestUtil.buildClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(NoOpCachingConfiguration.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DetailResponseTests {

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
    void cleanup() {
        buildClient(port).delete()
            .uri("/masters/{masterId}/details/{detailId}", masterId.get(), detailId.get())
            .exchange();
        buildClient(port).delete()
            .uri("/masters/{masterId}", masterId.get())
            .exchange();
    }

    private WebTestClient.BodyContentSpec validateFieldsTypes(WebTestClient.BodyContentSpec spec) {
        return spec
            .jsonPath("$.id").isNumber()
            .jsonPath("$.name").exists()
            .jsonPath("$.uri").exists();
    }

    private void validateUri(WebTestClient.BodyContentSpec spec) {
        var uri = String.format("http://localhost:%d/masters/%d/details/%d", port, masterId.get(), detailId.get());
        spec.jsonPath("$.uri").isEqualTo(uri);
    }

    @Test
    @Order(1)
    void whenDetailCreate_thenResponseBodyValid() {
        var body = """
            {
              "name": "Detail 1 in Master 1"
            }
            """;

        var spec = buildClient(port).post()
            .uri("/masters/{masterId}/details", masterId.get())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchangeSuccessfully()
            .expectBody();
        spec = validateFieldsTypes(spec)
            .jsonPath("$.id").value(Long.class, detailId::set)
            .jsonPath("$.name").isEqualTo("Detail 1 in Master 1");
        validateUri(spec);
    }

    @Test
    @Order(2)
    void whenDetailGet_thenResponseBodyValid() {
        var spec = buildClient(port).get()
            .uri("/masters/{masterId}/details/{detailId}", masterId.get(), detailId.get())
            .exchangeSuccessfully()
            .expectBody();
        spec = validateFieldsTypes(spec)
            .jsonPath("$.id").isEqualTo(detailId.get())
            .jsonPath("$.name").isEqualTo("Detail 1 in Master 1");
        validateUri(spec);
    }

    @Test
    @Order(3)
    void whenDetailUpdate_thenResponseBodyValid() {
        var body = """
            {
              "name": "Detail 1 in Master 1 (updated)"
            }
            """;

        var spec = buildClient(port).put()
            .uri("/masters/{masterId}/details/{detailId}", masterId.get(), detailId.get())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchangeSuccessfully()
            .expectBody();
        spec = validateFieldsTypes(spec)
            .jsonPath("$.id").isEqualTo(detailId.get())
            .jsonPath("$.name").isEqualTo("Detail 1 in Master 1 (updated)");
        validateUri(spec);
    }
}
