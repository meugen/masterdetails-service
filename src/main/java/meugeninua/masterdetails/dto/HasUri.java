package meugeninua.masterdetails.dto;

import org.springframework.web.util.UriBuilder;

import java.net.URI;

public interface HasUri {

    URI buildUri(UriBuilder builder);
}
