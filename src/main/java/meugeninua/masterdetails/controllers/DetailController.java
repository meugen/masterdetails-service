package meugeninua.masterdetails.controllers;

import jakarta.validation.Valid;
import meugeninua.masterdetails.dto.DetailDto;
import meugeninua.masterdetails.services.DetailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/masters/{masterId}/details")
public class DetailController {

    private final DetailService detailService;

    public DetailController(DetailService detailService) {
        this.detailService = detailService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Iterable<?> findAll(@PathVariable("masterId") Long masterId) {
        return detailService.findAll(masterId);
    }

    @GetMapping(value = "/{detailId}", produces =  MediaType.APPLICATION_JSON_VALUE)
    public Object findById(@PathVariable("masterId") Long masterId, @PathVariable("detailId") Long detailId) {
        return detailService.findById(masterId, detailId);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Object create(
        @PathVariable("masterId") Long masterId,
        @RequestBody @Valid DetailDto detailDto
    ) {
        return detailService.create(masterId, detailDto);
    }

    @PutMapping(value = "/{detailId}", produces =  MediaType.APPLICATION_JSON_VALUE)
    public Object update(
        @PathVariable("masterId") Long masterId,
        @PathVariable("detailId") Long detailId,
        @RequestBody @Valid DetailDto detailDto
    ) {
        return detailService.update(masterId, detailId, detailDto);
    }

    @DeleteMapping("/{detailId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(
        @PathVariable("masterId") Long masterId,
        @PathVariable("detailId") Long detailId
    ) {
        detailService.deleteById(masterId, detailId);
    }
}
