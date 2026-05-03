package meugeninua.masterdetails.controllers;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import meugeninua.masterdetails.annotations.ApiResponseCreated;
import meugeninua.masterdetails.annotations.ApiResponseNoContent;
import meugeninua.masterdetails.annotations.ApiResponseNotFound;
import meugeninua.masterdetails.annotations.ApiResponseOk;
import meugeninua.masterdetails.dto.DetailDto;
import meugeninua.masterdetails.services.DetailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/masters/{masterId}/details", produces = MediaType.APPLICATION_JSON_VALUE)
public class DetailController {

    private final DetailService detailService;

    public DetailController(DetailService detailService) {
        this.detailService = detailService;
    }

    @GetMapping
    @ApiResponseOk(content = @Content(
        array = @ArraySchema(schema = @Schema(implementation = DetailDto.class))
    ))
    public Iterable<?> findAll(@PathVariable Long masterId) {
        return detailService.findAll(masterId);
    }

    @GetMapping("/{detailId}")
    @ApiResponseOk(content = @Content(schema = @Schema(implementation = DetailDto.class)))
    @ApiResponseNotFound
    public Object findById(@PathVariable Long masterId, @PathVariable Long detailId) {
        return detailService.findById(masterId, detailId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponseCreated(content = @Content(schema = @Schema(implementation = DetailDto.class)))
    @ApiResponseNotFound
    public Object create(
        @PathVariable Long masterId,
        @RequestBody @Valid DetailDto detailDto
    ) {
        return detailService.create(masterId, detailDto);
    }

    @PutMapping("/{detailId}")
    @ApiResponseOk(content = @Content(schema = @Schema(implementation = DetailDto.class)))
    @ApiResponseNotFound
    public Object update(
        @PathVariable Long masterId,
        @PathVariable Long detailId,
        @RequestBody @Valid DetailDto detailDto
    ) {
        return detailService.update(masterId, detailId, detailDto);
    }

    @DeleteMapping("/{detailId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiResponseNoContent
    @ApiResponseNotFound
    public void deleteById(
        @PathVariable Long masterId,
        @PathVariable Long detailId
    ) {
        detailService.deleteById(masterId, detailId);
    }
}
