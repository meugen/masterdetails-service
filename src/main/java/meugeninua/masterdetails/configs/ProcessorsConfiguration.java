package meugeninua.masterdetails.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import meugeninua.masterdetails.caching.DetailCacheEvictProcessor;
import meugeninua.masterdetails.caching.MasterCacheEvictProcessor;
import meugeninua.masterdetails.processors.Processor;
import meugeninua.masterdetails.processors.ToMapProcessor;
import meugeninua.masterdetails.processors.WithUriProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriBuilder;

@Configuration
public class ProcessorsConfiguration {

    private static final String MASTER_URI_TEMPLATE = "/masters/{id}";
    private static final String DETAIL_URI_TEMPLATE = "/masters/{masterId}/details/{detailId}";

    @Bean("master")
    @RequestScope
    public Processor masterProcessor(
        ObjectMapper mapper,
        HttpServletRequest request,
        StringRedisTemplate redisTemplate
    ) {
        Processor processor = new ToMapProcessor(mapper);
        processor = new WithUriProcessor(processor, buildUriBuilder(MASTER_URI_TEMPLATE));
        if (!"GET".equals(request.getMethod())) {
            processor = new MasterCacheEvictProcessor(processor, redisTemplate);
        }
        return processor;
    }

    @Bean("detail")
    @RequestScope
    public Processor detailProcessor(
        ObjectMapper mapper,
        HttpServletRequest request,
        StringRedisTemplate redisTemplate
    ) {
        Processor processor = new ToMapProcessor(mapper);
        processor = new WithUriProcessor(processor, buildUriBuilder(DETAIL_URI_TEMPLATE));
        if (!"GET".equals(request.getMethod())) {
            processor = new DetailCacheEvictProcessor(processor, redisTemplate);
        }
        return processor;
    }

    private UriBuilder buildUriBuilder(String uriTemplate) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(uriTemplate);
    }

}
