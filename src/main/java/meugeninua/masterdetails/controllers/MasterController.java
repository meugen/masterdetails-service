package meugeninua.masterdetails.controllers;

import meugeninua.masterdetails.dto.MasterDto;
import meugeninua.masterdetails.services.MasterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.stream.Stream;

@RestController
@RequestMapping("/masters")
public class MasterController {

    private final MasterService masterService;

    public MasterController(MasterService masterService) {
        this.masterService = masterService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Stream<?> findAll() {
        var builder = ServletUriComponentsBuilder
            .fromCurrentContextPath()
            .path("/masters/{id}");
        return masterService.findAll().map(dto -> dto.withUri(builder));
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public MasterDto findById(@PathVariable("id") Long id) {
        return masterService.findById(id);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public MasterDto create(@RequestBody MasterDto master) {
        return masterService.create(master);
    }

    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public MasterDto update(@PathVariable("id") Long id, @RequestBody MasterDto master) {
        return masterService.update(id, master);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable("id") Long id) {
        masterService.deleteById(id);
    }
}
