package com.bookshop.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * Redis 缓存配置
 * - JSON 序列化（而非 Java 二进制）
 * - 设置默认过期时间
 */
@Configuration
public class RedisCacheConfig {

    private final RedisConnectionFactory redisConnectionFactory;

    public RedisCacheConfig(RedisConnectionFactory redisConnectionFactory) {
        this.redisConnectionFactory = redisConnectionFactory;
    }

    /**
     * 缓存管理器
     * 不同缓存名对应不同过期时间
     */
    @Bean
    public CacheManager cacheManager() {
        // 给每个缓存 key 加随机偏移，防止雪崩
        java.util.Random random = new java.util.Random();
        // 图书缓存 30 分钟
        RedisCacheConfiguration bookCacheConfig = defaultConfig().entryTtl(Duration.ofMinutes(25 + random.nextInt(10)));
        // 分类缓存 1 小时
        RedisCacheConfiguration categoryCacheConfig = defaultConfig().entryTtl(Duration.ofMinutes(50 + random.nextInt(20)));

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(defaultConfig().entryTtl(Duration.ofMinutes(8 + random.nextInt(5))))  // 默认 8~12 分钟
                .withCacheConfiguration("books", bookCacheConfig)
                .withCacheConfiguration("book", bookCacheConfig)
                .withCacheConfiguration("categories", categoryCacheConfig)
                .build();
    }

    /**
     * 默认序列化配置：Key 用字符串，Value 用 JSON
     */
    private RedisCacheConfiguration defaultConfig() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );

        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer(mapper);

        return RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jsonSerializer));
    }
}
