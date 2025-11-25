package meugeninua.masterdetails.configs;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

import java.util.logging.Logger;

@Configuration
@EnableCaching
public class CachingConfiguration {

    private final Logger logger = Logger.getLogger(CachingConfiguration.class.getName());

    @Bean
    public RedisConnectionFactory jedisConnectionFactory(Environment environment) {
        var hostname = environment.getProperty("REDIS_HOSTNAME", "localhost");
        var port = environment.getProperty("REDIS_PORT", Integer.class, 6379);
        var useSsl = environment.getProperty("REDIS_USE_SSL", Boolean.class, false);

        logger.info(String.format("Redis hostname: %s, port: %d, use-ssl: %b", hostname, port, useSsl));

        var configuration = new RedisStandaloneConfiguration(hostname, port);
        var clientConfigBuilder = JedisClientConfiguration.builder();
        if (useSsl) {
            clientConfigBuilder.useSsl();
        }

        return new JedisConnectionFactory(configuration, clientConfigBuilder.build());
    }
}
