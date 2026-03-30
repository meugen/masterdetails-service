package meugeninua.masterdetails.util;

import org.springframework.test.web.reactive.server.WebTestClient;

public class TestUtil {

    private TestUtil() {}

    public static WebTestClient buildClient(int port) {
        return buildClient("localhost", port);
    }

    public static WebTestClient buildClient(String host, int port) {
        return WebTestClient.bindToServer()
            .baseUrl(String.format("http://%s:%d", host, port))
            .build();
    }
}
