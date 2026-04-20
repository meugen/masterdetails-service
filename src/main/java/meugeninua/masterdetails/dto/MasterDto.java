package meugeninua.masterdetails.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.util.UriBuilder;

import java.net.URI;
import java.util.List;

public record MasterDto(
    Long id,
    @NotNull String name,
    @NotNull @Valid List<DetailDto> details,
    @NotNull Integer count
) implements HasUri {

    @Override
    public URI buildUri(UriBuilder builder) {
        return builder.build(id);
    }
}
