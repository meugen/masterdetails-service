package meugeninua.masterdetails.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.util.UriBuilder;

import java.net.URI;
import java.util.List;

@Schema(name = "Master", description = "Master resource with details")
public record MasterDto(
    @Schema(description = "Master identifier", example = "1")
    Long id,
    @Schema(description = "Master name", example = "Master 1")
    @NotNull String name,
    @Schema(description = "List of details associated with the master")
    @NotNull @Valid List<DetailDto> details,
    @Schema(description = "Count of details associated with the master", example = "2")
    @NotNull Integer count
) implements HasUri {

    @Override
    public URI buildUri(UriBuilder builder) {
        return builder.build(id);
    }
}
