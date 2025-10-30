package meugeninua.masterdetails.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.web.util.UriBuilder;

import java.net.URI;

public class DetailDto implements HasUri {

    private Long id;
    private String name;
    private Long masterId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonIgnore
    public Long getMasterId() {
        return masterId;
    }

    public void setMasterId(Long masterId) {
        this.masterId = masterId;
    }

    @Override
    public URI buildUri(UriBuilder builder) {
        return builder.build(masterId, id);
    }
}
