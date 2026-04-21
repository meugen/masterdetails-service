package meugeninua.masterdetails.caching;

import meugeninua.masterdetails.dto.DetailDto;
import meugeninua.masterdetails.processors.Processor;
import meugeninua.masterdetails.util.RedisUtil;

import java.util.Map;
import java.util.Set;

public class DetailCacheEvictProcessor implements Processor, CachingConstants {

    private final Processor baseProcessor;
    private final RedisUtil redisUtil;

    public DetailCacheEvictProcessor(
        Processor baseProcessor,
        RedisUtil redisUtil
    ) {
        this.baseProcessor = baseProcessor;
        this.redisUtil = redisUtil;
    }

    @Override
    public Map<String, Object> process(Object obj) {
        var result = baseProcessor.process(obj);
        if (obj instanceof DetailDto detailDto) {
            processDetailDto(detailDto);
        }
        return result;
    }

    private void processDetailDto(DetailDto detailDto) {
        var keys = Set.of(
            mastersListKey(),
            masterByIdKey(detailDto.masterId()),
            detailsListKey(detailDto.masterId()),
            detailByIdKey(detailDto.masterId(), detailDto.id())
        );
        redisUtil.delete(keys);
    }
}
