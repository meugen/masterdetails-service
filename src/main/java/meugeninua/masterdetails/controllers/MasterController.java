package meugeninua.masterdetails.controllers;

import meugeninua.masterdetails.dto.MasterDto;
import meugeninua.masterdetails.entities.Master;
import meugeninua.masterdetails.services.MasterService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/masters")
public class MasterController {

    private final MasterService masterService;

    public MasterController(MasterService masterService) {
        this.masterService = masterService;
    }

    @GetMapping("/{id}")
    public MasterDto findById(@PathVariable("id") Long id) {
        return masterService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MasterDto create(@RequestBody MasterDto master) {
        return masterService.create(master);
    }

    @PutMapping("/{id}")
    public MasterDto update(@PathVariable("id") Long id, @RequestBody MasterDto master) {
        return masterService.update(id, master);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable("id") Long id) {
        masterService.deleteById(id);
    }
}
