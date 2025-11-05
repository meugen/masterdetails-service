package meugeninua.masterdetails.controllers;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(NoResourceFoundException.class)
    public String handleNoResourceFound() {
        return "redirect:/swagger-ui/index.html";
    }
}
