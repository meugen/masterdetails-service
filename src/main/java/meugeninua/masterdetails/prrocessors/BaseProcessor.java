package meugeninua.masterdetails.prrocessors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseProcessor implements Processor {

    private final Processor baseProcessor;
    private final ObjectMapper mapper;

    public BaseProcessor(
        Processor baseProcessor,
        ObjectMapper mapper
    ) {
        this.baseProcessor = baseProcessor;
        this.mapper = mapper;
    }

    protected Map<String, Object> toMap(Object obj) {
        var result = baseProcessor.process(obj);
        if (result instanceof InternalMap map) {
            return map;
        }
        return mapper.convertValue(result, new TypeReference<>() {});
    }

    private interface InternalMap extends Map<String, Object> {}
}
