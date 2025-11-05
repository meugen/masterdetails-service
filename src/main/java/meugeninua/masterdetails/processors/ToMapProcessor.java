package meugeninua.masterdetails.processors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class ToMapProcessor implements Processor {

    private final ObjectMapper mapper;

    public ToMapProcessor(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Map<String, Object> process(Object obj) {
        return mapper.convertValue(obj, new TypeReference<>() {});
    }
}
