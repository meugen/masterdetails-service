package meugeninua.masterdetails.prrocessors;

import com.fasterxml.jackson.databind.ObjectMapper;
import meugeninua.masterdetails.dto.HasUri;
import org.springframework.web.util.UriBuilder;

public class WithUriProcessor extends BaseProcessor {

    private final UriBuilder uriBuilder;

    public WithUriProcessor(
        Processor baseProcessor,
        ObjectMapper mapper,
        UriBuilder uriBuilder
    ) {
        super(baseProcessor, mapper);
        this.uriBuilder = uriBuilder;
    }

    @Override
    public Object process(Object obj) {
        var map = toMap(obj);
        if (obj instanceof HasUri hasUri) {
            map.put("uri", hasUri.buildUri(uriBuilder));
        }
        return map;
    }
}
