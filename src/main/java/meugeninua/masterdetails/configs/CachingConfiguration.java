package meugeninua.masterdetails.configs;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

@Configuration
@EnableCaching
public class CachingConfiguration {

    @Bean
    public RedisConnectionFactory jedisConnectionFactory(Environment environment) {
        var hostname = environment.getProperty("REDIS_HOSTNAME", "localhost");
        var port = environment.getProperty("REDIS_PORT", Integer.class, 6379);
        return new JedisConnectionFactory(
            new RedisStandaloneConfiguration(hostname, port)
        );
    }
}
