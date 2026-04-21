package meugeninua.masterdetails.dto;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.util.UriBuilder;

import java.net.URI;

public record DetailDto(
    Long id,
    @NotNull String name,
    Long masterId
) implements HasUri {

    @Override
    public URI buildUri(UriBuilder builder) {
        return builder.build(masterId, id);
    }
}
