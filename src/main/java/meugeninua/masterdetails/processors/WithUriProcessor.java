package meugeninua.masterdetails.processors;

import meugeninua.masterdetails.dto.HasUri;
import org.springframework.web.util.UriBuilder;

import java.util.Map;

public class WithUriProcessor implements Processor {

    private final Processor baseProcessor;
    private final UriBuilder uriBuilder;

    public WithUriProcessor(Processor baseProcessor, UriBuilder uriBuilder) {
        this.baseProcessor = baseProcessor;
        this.uriBuilder = uriBuilder;
    }

    @Override
    public Map<String, Object> process(Object obj) {
        var result = baseProcessor.process(obj);
        if (obj instanceof HasUri hasUri) {
            result.put("uri", hasUri.buildUri(uriBuilder));
        }
        return result;
    }
}
