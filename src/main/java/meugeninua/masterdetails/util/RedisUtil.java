package meugeninua.masterdetails.util;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.SessionCallback;
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

    public long delete(Collection<String> keys) {
        if (keys.isEmpty()) return 0;

        var results = redisTemplate.executePipelined(new SessionCallback<>() {
            @Override
            public <K, V> Object execute(RedisOperations<K, V> operations) throws DataAccessException {
                for (var key : keys) {
                    redisTemplate.delete(key);
                }
                return null;
            }
        });
        return results.stream()
            .filter(Boolean.TRUE::equals)
            .count();
    }
}
