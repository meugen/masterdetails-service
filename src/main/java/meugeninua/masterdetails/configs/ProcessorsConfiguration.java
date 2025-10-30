package meugeninua.masterdetails.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import meugeninua.masterdetails.prrocessors.Processor;
import meugeninua.masterdetails.prrocessors.WithUriProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriBuilder;

@Configuration
public class ProcessorsConfiguration {

    private static final String MASTER_URI_TEMPLATE = "/masters/{id}";
    private static final String DETAIL_URI_TEMPLATE = "/masters/{masterId}/details/{detailId}";

    @Bean("master")
    @RequestScope
    public Processor masterProcessor(ObjectMapper mapper) {
        Processor processor = new EmptyProcessor();
        processor = new WithUriProcessor(processor, mapper, buildUriBuilder(MASTER_URI_TEMPLATE));
        return processor;
    }

    @Bean("detail")
    @RequestScope
    public Processor detailProcessor(ObjectMapper mapper) {
        Processor processor = new EmptyProcessor();
        processor = new WithUriProcessor(processor, mapper, buildUriBuilder(DETAIL_URI_TEMPLATE));
        return processor;
    }

    private UriBuilder buildUriBuilder(String uriTemplate) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(uriTemplate);
    }

    private static class EmptyProcessor implements Processor {
        @Override
        public Object process(Object obj) { return obj; }
    }
}
