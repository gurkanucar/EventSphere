package com.gucardev.eventsphere.infrastructure.config.cache;//package com.gucardev.eventsphere.infrastructure.config.cache;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.cache.CacheManager;
//import org.springframework.cache.annotation.EnableCaching;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//import org.springframework.data.redis.cache.RedisCacheConfiguration;
//import org.springframework.data.redis.cache.RedisCacheManager;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
//import org.springframework.data.redis.serializer.RedisSerializationContext;
//import org.springframework.data.redis.serializer.StringRedisSerializer;
//
//import java.time.Duration;
//import java.util.HashSet;
//
//@Slf4j
//@EnableCaching
//@Configuration
//@RequiredArgsConstructor
//public class RedisCacheConfig {
//
//    // Cache Manager Bean Names
//    public static final String REDIS_CACHE_MANAGER_ONE_MINUTE = "redisOneMinuteCacheManager";
//    public static final String REDIS_CACHE_MANAGER_TWO_MINUTES = "redisTwoMinutesCacheManager";
//    public static final String REDIS_CACHE_MANAGER_FIVE_MINUTES = "redisFiveMinutesCacheManager";
//    public static final String REDIS_CACHE_MANAGER_TEN_MINUTES = "redisTenMinutesCacheManager";
//    public static final String REDIS_CACHE_MANAGER_THIRTY_MINUTES = "redisThirtyMinutesCacheManager";
//
//    @Bean
//    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
//        RedisTemplate<String, Object> template = new RedisTemplate<>();
//        template.setConnectionFactory(connectionFactory);
//
//        GenericJacksonJsonRedisSerializer jsonSerializer = GenericJacksonJsonRedisSerializer.builder()
//                .enableUnsafeDefaultTyping()
//                .build();
//
//        template.setKeySerializer(new StringRedisSerializer());
//        template.setValueSerializer(jsonSerializer);
//        template.setHashKeySerializer(new StringRedisSerializer());
//        template.setHashValueSerializer(jsonSerializer);
//
//        template.afterPropertiesSet();
//        return template;
//    }
//
//    @Primary
//    @Bean(REDIS_CACHE_MANAGER_ONE_MINUTE)
//    public CacheManager redisOneMinuteCacheManager(RedisConnectionFactory connectionFactory) {
//        log.info("Creating 1-minute cache manager with TTL: {}", Duration.ofMinutes(1));
//        return buildRedisCacheManager(connectionFactory, Duration.ofMinutes(1));
//    }
//
//    @Bean(REDIS_CACHE_MANAGER_TWO_MINUTES)
//    public CacheManager redisTwoMinutesCacheManager(RedisConnectionFactory connectionFactory) {
//        log.info("Creating 2-minutes cache manager with TTL: {}", Duration.ofMinutes(2));
//        return buildRedisCacheManager(connectionFactory, Duration.ofMinutes(2));
//    }
//
//    @Bean(REDIS_CACHE_MANAGER_FIVE_MINUTES)
//    public CacheManager redisFiveMinutesCacheManager(RedisConnectionFactory connectionFactory) {
//        log.info("Creating 5-minutes cache manager with TTL: {}", Duration.ofMinutes(5));
//        return buildRedisCacheManager(connectionFactory, Duration.ofMinutes(5));
//    }
//
//    @Bean(REDIS_CACHE_MANAGER_TEN_MINUTES)
//    public CacheManager redisTenMinutesCacheManager(RedisConnectionFactory connectionFactory) {
//        log.info("Creating 10-minutes cache manager with TTL: {}", Duration.ofMinutes(10));
//        return buildRedisCacheManager(connectionFactory, Duration.ofMinutes(10));
//    }
//
//    @Bean(REDIS_CACHE_MANAGER_THIRTY_MINUTES)
//    public CacheManager redisThirtyMinutesCacheManager(RedisConnectionFactory connectionFactory) {
//        log.info("Creating 30-minutes cache manager with TTL: {}", Duration.ofMinutes(30));
//        return buildRedisCacheManager(connectionFactory, Duration.ofMinutes(30));
//    }
//
//    private CacheManager buildRedisCacheManager(RedisConnectionFactory connectionFactory, Duration ttl) {
//        GenericJacksonJsonRedisSerializer jsonSerializer = GenericJacksonJsonRedisSerializer.builder()
//                .enableUnsafeDefaultTyping()
//                .build();
//
//        RedisCacheConfiguration cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
//                .entryTtl(ttl)
//                .serializeValuesWith(
//                        RedisSerializationContext.SerializationPair.fromSerializer(jsonSerializer)
//                );
//
//        return RedisCacheManager.builder(connectionFactory)
//                .cacheDefaults(cacheConfiguration)
//                .initialCacheNames(new HashSet<>(CacheNames.getAllCacheNames()))
//                .build();
//    }
//}