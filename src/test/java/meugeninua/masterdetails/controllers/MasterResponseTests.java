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
class MasterResponseTests {

    @LocalServerPort
    private int port;

    private final AtomicLong masterId = new AtomicLong();

    @AfterAll
    void deleteMaster() {
        buildClient(port).delete()
            .uri("/masters/{masterId}", masterId.get())
            .exchange();
    }

    private WebTestClient.BodyContentSpec validateFieldsTypes(WebTestClient.BodyContentSpec spec, ResponseType responseType) {
        return spec
            .jsonPath(responseType.buildExpression(".id")).isNumber()
            .jsonPath(responseType.buildExpression(".name")).exists()
            .jsonPath(responseType.buildExpression(".details")).isArray()
            .jsonPath(responseType.buildExpression(".count")).isNumber()
            .jsonPath(responseType.buildExpression(".uri")).exists()
            .jsonPath(responseType.buildExpression(".details[0].id")).isNumber()
            .jsonPath(responseType.buildExpression(".details[0].name")).exists();
    }

    private void validateUri(WebTestClient.BodyContentSpec spec, ResponseType responseType) {
        var uri = String.format("http://localhost:%d/masters/%d", port, masterId.get());
        spec.jsonPath(responseType.buildExpression(".uri")).isEqualTo(uri);
    }

    @Test
    @Order(1)
    void whenMasterCreate_thenResponseBodyValid() {
        var body = """
            {
              "name": "Master 1",
              "details": [
                {
                  "name": "Detail 1 in Master 1"
                }
              ],
              "count": 1
            }
            """;

        var spec = buildClient(port).post()
            .uri("/masters")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchangeSuccessfully()
            .expectBody();
        spec = validateFieldsTypes(spec, ResponseType.SINGLE)
            .jsonPath("$.id").value(Long.class, masterId::set)
            .jsonPath("$.name").isEqualTo("Master 1")
            .jsonPath("$.count").isEqualTo(1)
            .jsonPath("$.details[0].name").isEqualTo("Detail 1 in Master 1");
        validateUri(spec, ResponseType.SINGLE);
    }

    @Test
    @Order(2)
    void whenMasterGet_thenResponseBodyValid() {
        var spec = buildClient(port).get()
            .uri("/masters/{masterId}", masterId.get())
            .exchangeSuccessfully()
            .expectBody();
        spec = validateFieldsTypes(spec, ResponseType.SINGLE)
            .jsonPath("$.id").isEqualTo(masterId.get())
            .jsonPath("$.name").isEqualTo("Master 1")
            .jsonPath("$.count").isEqualTo(1)
            .jsonPath("$.details[0].name").isEqualTo("Detail 1 in Master 1");
        validateUri(spec, ResponseType.SINGLE);
    }

    @Test
    @Order(3)
    void whenMasterUpdate_thenResponseBodyValid() {
        var body = """
            {
              "name": "Master 1 (updated)",
              "details": [
                {
                  "name": "Detail 1 in Master 1 (updated)"
                }
              ],
              "count": 2
            }
            """;

        var spec = buildClient(port).put()
            .uri("/masters/{masterId}", masterId.get())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchangeSuccessfully()
            .expectBody();
        spec = validateFieldsTypes(spec, ResponseType.SINGLE)
            .jsonPath("$.id").isEqualTo(masterId.get())
            .jsonPath("$.name").isEqualTo("Master 1 (updated)")
            .jsonPath("$.count").isEqualTo(2)
            .jsonPath("$.details[0].name").isEqualTo("Detail 1 in Master 1 (updated)");
        validateUri(spec, ResponseType.SINGLE);
    }

    @Test
    @Order(4)
    void whenMasterGetAll_thenResponseBodyValid() {
        var spec = buildClient(port).get()
            .uri("/masters")
            .exchangeSuccessfully()
            .expectBody()
            .jsonPath("$").isArray();
        spec = validateFieldsTypes(spec, ResponseType.ARRAY)
            .jsonPath("$[0].id").isEqualTo(masterId.get())
            .jsonPath("$[0].name").isEqualTo("Master 1 (updated)")
            .jsonPath("$[0].count").isEqualTo(2)
            .jsonPath("$[0].details[0].name").isEqualTo("Detail 1 in Master 1 (updated)");
        validateUri(spec, ResponseType.ARRAY);
    }

    private enum ResponseType {
        SINGLE(""), ARRAY("[0]");

        private final String value;
        ResponseType(String value) {
            this.value = value;
        }

        public String buildExpression(String suffix) {
            return "$" + value + suffix;
        }
    }
}
