package meugeninua.masterdetails.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.util.UriBuilder;

import java.net.URI;

@Schema(name = "Detail", description = "Detail resource")
public record DetailDto(
    @Schema(description = "Detail identifier", example = "1")
    Long id,
    @Schema(description = "Detail name", example = "Detail 1")
    @NotNull String name,
    @Schema(description = "Master identifier", example = "1", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    Long masterId
) implements HasUri {

    @Override
    public URI buildUri(UriBuilder builder) {
        return builder.build(masterId, id);
    }
}
