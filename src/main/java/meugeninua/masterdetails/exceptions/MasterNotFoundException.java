package meugeninua.masterdetails.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class MasterNotFoundException extends ResponseStatusException {
    public MasterNotFoundException(Long id) {
        super(HttpStatus.NOT_FOUND, String.format("Master with id %d is not found", id));
    }
}
