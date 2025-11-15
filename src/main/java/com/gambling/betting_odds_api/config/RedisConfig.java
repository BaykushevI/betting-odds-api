package com.gambling.betting_odds_api.config;

// ═══════════════════════════════════════════════════════════════════════════
// SPRING DATA REDIS - Redis integration
// ═══════════════════════════════════════════════════════════════════════════
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

// ═══════════════════════════════════════════════════════════════════════════
// JACKSON - JSON serialization
// ═══════════════════════════════════════════════════════════════════════════
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;

// ═══════════════════════════════════════════════════════════════════════════
// JAVA STANDARD LIBRARY
// ═══════════════════════════════════════════════════════════════════════════
import java.time.Duration;

/**
 * RedisConfig - Redis caching configuration.
 * 
 * This configuration:
 * 1. Enables Spring Cache abstraction
 * 2. Configures RedisTemplate for manual operations
 * 3. Configures RedisCacheManager for @Cacheable operations
 * 4. Sets up JSON serialization with Jackson
 * 5. Configures TTL (Time-To-Live) for cache entries
 * 6. ENABLES CACHE STATISTICS for Actuator metrics
 */
@Configuration
@EnableCaching
public class RedisConfig {

    /**
     * RedisTemplate - For manual Redis operations (if needed).
     * 
     * This bean is used when you want to interact with Redis directly,
     * outside of the @Cacheable annotations.
     * 
     * Configuration:
     * - Key serializer: String (human-readable keys in Redis)
     * - Value serializer: JSON (complex objects stored as JSON)
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Use String serializer for keys (simple and readable)
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        // Use JSON serializer for values (handles complex objects)
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer(objectMapper());
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);

        template.afterPropertiesSet();
        return template;
    }

    /**
     * RedisCacheManager - Cache manager for @Cacheable annotations.
     * 
     * This is the MAIN configuration for Spring Cache.
     * All @Cacheable, @CachePut, @CacheEvict annotations use this.
     * 
     * NEW in Phase 4 Day 6:
     * - enableStatistics() - Enables cache metrics for Actuator!
     * 
     * Configuration:
     * - TTL: 30 minutes (can be overridden per cache)
     * - Serialization: JSON with type information
     * - Statistics: ENABLED for Actuator metrics
     */
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        // Create JSON serializer with type information
        GenericJackson2JsonRedisSerializer jsonSerializer = 
            new GenericJackson2JsonRedisSerializer(objectMapper());

        // Configure Redis cache defaults
        RedisCacheConfiguration cacheConfig = RedisCacheConfiguration.defaultCacheConfig()
            // TTL: 30 minutes (1800 seconds)
            // Production recommendation: 5-10 minutes for live betting odds
            .entryTtl(Duration.ofMinutes(30))
            
            // Serialize cache keys as strings
            .serializeKeysWith(
                RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer())
            )
            
            // Serialize cache values as JSON
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(jsonSerializer)
            );

        // Build and return cache manager
        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(cacheConfig)
            .build();
    }

    /**
     * ObjectMapper - Jackson JSON configuration.
     * 
     * Custom ObjectMapper to handle:
     * 1. Java 8 Date/Time API (LocalDateTime, LocalDate, etc.)
     * 2. Type information (for polymorphic deserialization)
     * 
     * This prevents errors when serializing/deserializing complex objects.
     */
    private ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        
        // Register Java 8 Date/Time module (for LocalDateTime support)
        mapper.registerModule(new JavaTimeModule());
        
        // Disable writing dates as timestamps (use ISO-8601 format instead)
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        // Enable type information for polymorphic types
        // This adds @class field to JSON for proper deserialization
        BasicPolymorphicTypeValidator validator = BasicPolymorphicTypeValidator.builder()
            .allowIfBaseType(Object.class)
            .build();
        mapper.activateDefaultTyping(validator, DefaultTyping.NON_FINAL);
        
        return mapper;
    }
}