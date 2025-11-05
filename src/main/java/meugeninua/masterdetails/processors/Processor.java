package meugeninua.masterdetails.processors;

import java.util.Map;

public interface Processor {

    Map<String, Object> process(Object obj);
}
