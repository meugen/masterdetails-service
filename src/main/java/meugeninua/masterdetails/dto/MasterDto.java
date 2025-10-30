package meugeninua.masterdetails.dto;

import org.springframework.web.util.UriBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MasterDto {

    private Long id;
    private String name;
    private List<DetailDto> details;
    private Integer count;

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

    public List<DetailDto> getDetails() {
        return details;
    }

    public void setDetails(List<DetailDto> details) {
        this.details = details;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Map<String, ?> withUri(UriBuilder builder) {
        var result = new HashMap<String, Object>();
        result.put("resource", this);
        result.put("uri", builder.build(id).toString());
        return result;
    }
}
