package com.titanium.data.redis.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.TransportMode;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Slf4j
@Configuration
public class TitaniumRedisConfiguration {

    @Bean
    @ConditionalOnMissingBean(RedisConnectionFactory.class)
    public LettuceConnectionFactory lettuceConnectionFactory(RedisProperties redisProperties) {
        log.info("TitaniumRedisConfiguration LettuceConnectionFactory standalone 初始化");
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(redisProperties.getHost());
        configuration.setPort(redisProperties.getPort());
        configuration.setUsername(redisProperties.getUsername());
        configuration.setPassword(redisProperties.getPassword());
        configuration.setDatabase(redisProperties.getDatabase());
        LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory();

        return connectionFactory;
    }

    @Bean
    public RedisTemplate<Object, Object> redisTemplate(LettuceConnectionFactory connectionFactory, ObjectMapper objectMapper) {
        log.info("TitaniumRedisConfiguration RedisTemplate 初始化");
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);

        //序列化设置
        Jackson2JsonRedisSerializer<Object> objectJackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(objectMapper, Object.class);

        //key 序列化 设置为 StringRedisSerializer
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        //value 序列化 设置为 Jackson2JsonRedisSerializer
        redisTemplate.setValueSerializer(objectJackson2JsonRedisSerializer);

        //hash key 序列化 设置为 StringRedisSerializer
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        //hash value 序列化 设置为 Jackson2JsonRedisSerializer
        redisTemplate.setHashValueSerializer(objectJackson2JsonRedisSerializer);

        return redisTemplate;
    }

    @Bean
    @ConditionalOnMissingBean(RedissonClient.class)
    public RedissonClient redisson(RedisProperties redisProperties) {
        log.info("TitaniumRedisConfiguration RedissonClient 初始化");
        Config config = new Config();
        if (System.getProperty("os.name").toLowerCase().contains("linux")) {
            config.setTransportMode(TransportMode.EPOLL);
        } else {
            config.setTransportMode(TransportMode.NIO);
        }
        config.useSingleServer()
                .setAddress("redis://" + redisProperties.getHost() + ":" + redisProperties.getPort()).setDatabase(redisProperties.getDatabase())
                .setPassword(redisProperties.getPassword())
                .setUsername(redisProperties.getUsername())
                .setDatabase(redisProperties.getDatabase());
        return Redisson.create(config);
    }

}
