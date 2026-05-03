package meugeninua.masterdetails.controllers;

import jakarta.servlet.http.HttpServletRequest;
import meugeninua.masterdetails.dto.ErrorDto;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(NoResourceFoundException.class)
    public String handleNoResourceFound() {
        return "redirect:/swagger-ui/index.html";
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorDto> handleResponseStatusException(
        ResponseStatusException ex,
        HttpServletRequest request
    ) {
        return ResponseEntity.status(ex.getStatusCode())
            .body(new ErrorDto(ex, buildUri(request)));
    }

    private String buildUri(HttpServletRequest request) {
        var url = request.getRequestURI();
        var queryString = request.getQueryString();
        if (!ObjectUtils.isEmpty(queryString)) {
            url += "?" + queryString;
        }
        return url;
    }
}
