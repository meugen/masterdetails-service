package meugeninua.masterdetails.controllers;

import meugeninua.masterdetails.dto.DetailDto;
import meugeninua.masterdetails.prrocessors.Processor;
import meugeninua.masterdetails.services.DetailService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Stream;

@Controller
@RequestMapping("/masters/{masterId}/details")
public class DetailController {

    private final DetailService detailService;
    private final Processor detailProcessor;

    public DetailController(
        DetailService detailService,
        @Qualifier("detail") Processor detailProcessor
    ) {
        this.detailService = detailService;
        this.detailProcessor = detailProcessor;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Iterable<?> findAll(@PathVariable("masterId") Long masterId) {
        return detailService.findAll(masterId).map(detailProcessor::process).toList();
    }

    @GetMapping(value = "/{detailId}", produces =  MediaType.APPLICATION_JSON_VALUE)
    public Object findById(@PathVariable("masterId") Long masterId, @PathVariable("detailId") Long detailId) {
        var result = detailService.findById(masterId, detailId);
        return detailProcessor.process(result);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Object create(@PathVariable("masterId") Long masterId, @RequestBody DetailDto detailDto) {
        var result = detailService.create(masterId, detailDto);
        return detailProcessor.process(result);
    }

    @PutMapping(value = "/{detailId}", produces =  MediaType.APPLICATION_JSON_VALUE)
    public Object update(@PathVariable("masterId") Long masterId, @PathVariable("detailId") Long detailId, @RequestBody DetailDto detailDto) {
        var result = detailService.update(masterId, detailId, detailDto);
        return detailProcessor.process(result);
    }

    @DeleteMapping("/{detailId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable("masterId") Long masterId, @PathVariable("detailId") Long detailId) {
        detailService.deleteById(masterId, detailId);
    }
}
