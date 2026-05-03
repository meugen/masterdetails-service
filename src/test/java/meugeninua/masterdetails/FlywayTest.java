package meugeninua.masterdetails;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.Ordered;

@SpringBootTest
@Order(Ordered.HIGHEST_PRECEDENCE)
class FlywayTest {

	@Test
	void whenApplicationStarted_flywayCreateSchema() {
	}

}
