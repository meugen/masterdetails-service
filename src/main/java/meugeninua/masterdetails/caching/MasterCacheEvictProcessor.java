package meugeninua.masterdetails.caching;

import meugeninua.masterdetails.dto.MasterDto;
import meugeninua.masterdetails.processors.Processor;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.HashSet;
import java.util.Map;

public class MasterCacheEvictProcessor implements Processor, CachingConstants {

    private final Processor baseProcessor;
    private final StringRedisTemplate redisTemplate;

    public MasterCacheEvictProcessor(
        Processor baseProcessor,
        StringRedisTemplate redisTemplate
    ) {
        this.baseProcessor = baseProcessor;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Map<String, Object> process(Object obj) {
        var result = baseProcessor.process(obj);
        if (obj instanceof MasterDto masterDto) {
            processMasterDto(masterDto);
        }
        return result;
    }

    private void processMasterDto(MasterDto masterDto) {
        var detailByIdPattern = String.format("%s::%d/*", CACHE_DETAIL_BY_ID, masterDto.getId());
        var keys = new HashSet<>(redisTemplate.keys(detailByIdPattern));
        keys.add(String.format("%s::%d", CACHE_DETAILS_LIST, masterDto.getId()));
        keys.add(String.format("%s::%d", CACHE_MASTER_BY_ID, masterDto.getId()));
        keys.add(String.format("%s::", CACHE_MASTERS_LIST));
        redisTemplate.delete(keys);
    }
}
