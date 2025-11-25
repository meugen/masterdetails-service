package meugeninua.masterdetails.processors;

import java.util.Map;

public class TempFieldProcessor implements Processor {

    private final Processor baseProcessor;

    public TempFieldProcessor(Processor baseProcessor) {
        this.baseProcessor = baseProcessor;
    }

    @Override
    public Map<String, Object> process(Object obj) {
        var result = baseProcessor.process(obj);
        result.put("temp", "Hello world");
        return result;
    }
}
