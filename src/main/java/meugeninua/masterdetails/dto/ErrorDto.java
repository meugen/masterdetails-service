package meugeninua.masterdetails.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Schema(name = "Error", description = "Error response")
public record ErrorDto(
    @Schema(description = "HTTP status code", example = "404")
    int status,
    @Schema(description = "Error message", example = "Resource with id 1 is not found")
    String message,
    @Schema(description = "Timestamp of the error", example = "2024-06-01T12:00:00.123456")
    LocalDateTime timestamp,
    @Schema(description = "Request URI that caused the error", example = "/masters/1")
    String uri
) {

    public ErrorDto(ResponseStatusException ex, String uri) {
        this(ex.getStatusCode().value(), ex.getReason(), LocalDateTime.now(), uri);
    }
}
