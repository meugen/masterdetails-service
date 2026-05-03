package meugeninua.masterdetails.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class DetailNotFoundException extends ResponseStatusException {
    public DetailNotFoundException(Long masterId, Long detailId) {
        super(HttpStatus.NOT_FOUND, String.format("Detail with id %d (Master id %d) is not found", detailId, masterId));
    }
}
