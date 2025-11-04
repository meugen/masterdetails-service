package meugeninua.masterdetails.controllers;

import jakarta.validation.Valid;
import meugeninua.masterdetails.dto.MasterDto;
import meugeninua.masterdetails.services.MasterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/masters")
public class MasterController {

    private final MasterService masterService;

    public MasterController(MasterService masterService) {
        this.masterService = masterService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Iterable<?> findAll() {
        return masterService.findAll();
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object findById(@PathVariable("id") Long id) {
        return masterService.findById(id);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Object create(@RequestBody @Valid MasterDto master) {
        return masterService.create(master);
    }

    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object update(
        @PathVariable("id") Long id,
        @RequestBody @Valid MasterDto master
    ) {
        return masterService.update(id, master);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable("id") Long id) {
        masterService.deleteById(id);
    }
}
