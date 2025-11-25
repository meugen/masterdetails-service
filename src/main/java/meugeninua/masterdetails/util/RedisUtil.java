package meugeninua.masterdetails.util;

import jakarta.annotation.Nullable;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Component
public class RedisUtil {

    private static final int COUNT = 1000;

    private final StringRedisTemplate redisTemplate;

    public RedisUtil(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Set<String> keys(String pattern) {
        var result = new HashSet<String>();

        var options = ScanOptions.scanOptions()
            .match(pattern)
            .count(COUNT)
            .build();
        try (var cursor = redisTemplate.scan(options)) {
            while (cursor.hasNext()) {
                result.add(cursor.next());
            }
        }
        return result;
    }

    @Nullable
    public Long delete(Collection<String> keys) {
        Long result = null;
        for (var key : keys) {
            if (Boolean.TRUE.equals(redisTemplate.delete(key))) {
                result = result == null ? 0L : result + 1;
            }
        }
        return result;
    }
}
