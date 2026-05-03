package meugeninua.masterdetails.controllers;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import meugeninua.masterdetails.annotations.ApiResponseCreated;
import meugeninua.masterdetails.annotations.ApiResponseNoContent;
import meugeninua.masterdetails.annotations.ApiResponseNotFound;
import meugeninua.masterdetails.annotations.ApiResponseOk;
import meugeninua.masterdetails.dto.MasterDto;
import meugeninua.masterdetails.services.MasterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/masters", produces = MediaType.APPLICATION_JSON_VALUE)
public class MasterController {

    private final MasterService masterService;

    public MasterController(MasterService masterService) {
        this.masterService = masterService;
    }

    @GetMapping
    @ApiResponseOk(content = @Content(
        array = @ArraySchema(schema = @Schema(implementation = MasterDto.class))
    ))
    public Iterable<?> findAll() {
        return masterService.findAll();
    }

    @GetMapping("/{id}")
    @ApiResponseOk(content = @Content(schema = @Schema(implementation = MasterDto.class)))
    @ApiResponseNotFound
    public Object findById(@PathVariable Long id) {
        return masterService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponseCreated(content = @Content(schema = @Schema(implementation = MasterDto.class)))
    public Object create(@RequestBody @Valid MasterDto master) {
        return masterService.create(master);
    }

    @PutMapping("/{id}")
    @ApiResponseOk(content = @Content(schema = @Schema(implementation = MasterDto.class)))
    @ApiResponseNotFound
    public Object update(
        @PathVariable Long id,
        @RequestBody @Valid MasterDto master
    ) {
        return masterService.update(id, master);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiResponseNoContent
    @ApiResponseNotFound
    public void deleteById(@PathVariable Long id) {
        masterService.deleteById(id);
    }
}
