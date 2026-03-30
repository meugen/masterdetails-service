package meugeninua.masterdetails.controllers;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static meugeninua.masterdetails.util.TestUtil.buildClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MasterCrudTests {

    @LocalServerPort
    private int port;

    private static Number masterId;

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
            .bodyValue(body)
            .header("Content-Type", "application/json")
            .exchange()
            .expectStatus().isCreated()
            .expectBody()
            .jsonPath("$.id").isNumber()
            .consumeWith(response -> {
                String responseBody = new String(response.getResponseBody());
                masterId = JsonPath.read(responseBody, "$.id");
            });
    }

    @Test
    @Order(2)
    void whenMasterGet_thenResponseOk() {
        buildClient(port).get()
            .uri("/masters/{masterId}", masterId)
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
            .uri("/masters/{masterId}", masterId)
            .bodyValue(body)
            .header("Content-Type", "application/json")
            .exchange()
            .expectStatus().isOk();
    }

    @Test
    @Order(4)
    void whenMasterDelete_thenResponseNoContent() {
        buildClient(port).delete()
            .uri("/masters/{masterId}", masterId)
            .exchange()
            .expectStatus().isNoContent();
    }

    @Test
    @Order(5)
    void whenMasterDeleted_thenResponseNotFound() {
        buildClient(port).get()
            .uri("/masters/{masterId}", masterId)
            .exchange()
            .expectStatus().isNotFound();
    }
}
