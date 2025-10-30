package meugeninua.masterdetails.controllers;

import meugeninua.masterdetails.dto.MasterDto;
import meugeninua.masterdetails.prrocessors.Processor;
import meugeninua.masterdetails.services.MasterService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Stream;

@RestController
@RequestMapping("/masters")
public class MasterController {

    private final MasterService masterService;
    private final Processor masterProcessor;

    public MasterController(
        MasterService masterService,
        @Qualifier("master") Processor masterProcessor
    ) {
        this.masterService = masterService;
        this.masterProcessor = masterProcessor;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Stream<?> findAll() {
        return masterService.findAll().map(masterProcessor::process);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object findById(@PathVariable("id") Long id) {
        var result = masterService.findById(id);
        return masterProcessor.process(result);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Object create(@RequestBody MasterDto master) {
        var result = masterService.create(master);
        return masterProcessor.process(result);
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
