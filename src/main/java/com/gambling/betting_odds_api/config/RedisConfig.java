package com.gambling.betting_odds_api.config;

// Spring Framework - Redis and caching
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

// Jackson - JSON processing with Java 8 Time support
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.introspect.TypeResolutionContext.Basic;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

// Java Standard Library
import java.time.Duration;

/**
 * RedisConfig - Configuration for Redis caching with TTL support.
 * 
 * Purpose:
 *   - Configure Redis connection
 *   - Define RedisTemplate for manual Redis operations
 *   - Define CacheManager for annotation-driven caching (@Cacheable, @CacheEvict)
 *   - Configure TTL (Time-To-Live) for automatic cache expiration
 *   - Configure serialization (how data is stored in Redis)
 * 
 * Redis Data Storage Format:
 *   - Keys: String (e.g., "odds::123")
 *   - Values: JSON (e.g., {"id":123,"sport":"Football",...})
 * 
 * TTL Strategy:
 *   - Current: 30 minutes (good for development/testing)
 *   - Production: Consider 1-5 minutes for live odds (they change frequently!)
 *   - Adjust Duration.ofMinutes(30) in cacheManager() method as needed
 * 
 * Connection:
 *   - Spring Boot auto-configures connection using application.properties
 *   - RedisConnectionFactory is auto-created by Spring Boot
 *   - We configure RedisTemplate and CacheManager
 */
@Configuration
@EnableCaching  // Enable Spring's annotation-driven cache management
public class RedisConfig {
    /**
     * Create ObjectMapper with type information enabled.
     * 
     * Type Information:
     *   - Stores class name in JSON: ["com.gambling...OddsResponse", {...}]
     *   - Allows correct deserialization (no LinkedHashMap casting errors)
     *   - Required for complex objects with inheritance/polymorphism
     * 
     * Security:
     *   - BasicPolymorphicTypeValidator restricts allowed classes
     *   - Only allows our package classes to be deserialized
     *   - Prevents security vulnerabilities
     * 
     * @return Configured ObjectMapper
     */
    private ObjectMapper createObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        
        // Register Java 8 Time module for LocalDateTime support
        objectMapper.registerModule(new JavaTimeModule());
        
        // Write dates as ISO-8601 strings, not timestamps
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        // Enable default typing for non-final types
        // This stores class information in JSON: ["ClassName", {...}]
        PolymorphicTypeValidator typeValidator = BasicPolymorphicTypeValidator
            .builder()
            .allowIfSubType("com.gambling.betting_odds_api")  // Our DTOs
            .allowIfSubType("java.time")   // LocalDateTime, LocalDate
            .allowIfSubType("java.util")   // Collections
            .allowIfSubType("java.math")   // BigDecimal ← ДОБАВИ ТОВА!
            .allowIfSubType("java.lang")   // String, Integer, etc.
            .build();
        
        objectMapper.activateDefaultTyping(
            typeValidator,
            ObjectMapper.DefaultTyping.NON_FINAL
        );
        
        return objectMapper;
    }
    /**
     * Configure RedisTemplate for manual Redis operations.
     * 
     * Use Cases:
     *   - Direct Redis operations (not through @Cacheable)
     *   - Custom caching logic
     *   - Testing and debugging
     * 
     * RedisTemplate provides:
     *   - opsForValue() - Simple key-value operations
     *   - opsForHash() - Hash operations
     *   - opsForList() - List operations
     *   - opsForSet() - Set operations
     *   - etc.
     * 
     * Serialization:
     *   - Keys: String (plain text)
     *   - Values: JSON (human-readable, language-agnostic)
     * 
     * @param connectionFactory Auto-injected by Spring Boot
     * @return Configured RedisTemplate
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(
            RedisConnectionFactory connectionFactory) {
        
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        
        // String serializer for keys
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);
        
        // JSON serializer for values with Java 8 Time support
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        Jackson2JsonRedisSerializer<Object> jsonSerializer = 
            new Jackson2JsonRedisSerializer<>(objectMapper, Object.class);
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);
        
        template.afterPropertiesSet();
        
        return template;
    }
    
    /**
     * Configure CacheManager for annotation-driven caching with TTL.
     * 
     * CacheManager handles:
     *   - @Cacheable - Automatically cache method results
     *   - @CacheEvict - Remove entries from cache
     *   - @CachePut - Update cache with new values
     *   - TTL - Automatic expiration of cached entries
     * 
     * TTL Configuration:
     *   - Current: 30 minutes (Duration.ofMinutes(30))
     *   - Good for development/testing (don't have to wait long)
     *   - Production recommendation: 1-5 minutes for live betting odds
     *     (odds change frequently in real gambling systems!)
     * 
     * Why TTL?
     *   - Prevents serving stale/outdated data
     *   - Automatic cleanup (no manual intervention needed)
     *   - Memory management (Redis doesn't grow indefinitely)
     * 
     * How TTL works:
     *   1. First request: Data cached with 30-minute expiration
     *   2. Subsequent requests: Served from cache (fast!)
     *   3. After 30 minutes: Redis automatically deletes the entry
     *   4. Next request: Cache miss, fetch from DB, cache again
     * 
     * Serialization:
     *   - Same as RedisTemplate (String keys, JSON values)
     *   - Ensures consistency across manual and automatic caching
     * 
     * @param connectionFactory Auto-injected by Spring Boot
     * @return Configured CacheManager with TTL support
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        
        // String serializer for cache keys
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        
        // JSON serializer with type information
        ObjectMapper objectMapper = createObjectMapper();
        GenericJackson2JsonRedisSerializer jsonSerializer = 
            new GenericJackson2JsonRedisSerializer(objectMapper);
        
        // Configure cache behavior
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
            // TTL: 30 minutes
            // PRODUCTION NOTE: Reduce to 1-5 minutes for live betting odds!
            .entryTtl(Duration.ofMinutes(30))
            
            // Serialize cache keys as Strings
            // Example: "odds::123"
            .serializeKeysWith(
                RedisSerializationContext.SerializationPair.fromSerializer(stringSerializer)
            )
            
            // Serialize cache values as JSON
            // Example: {"id":123,"sport":"Football","homeTeam":"Barcelona",...}
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(jsonSerializer)
            )
            
            // Optional: Don't cache null values
            // If a method returns null, don't store it in Redis
            .disableCachingNullValues();
        
        // Build and return CacheManager
        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(config)  // Apply configuration to all caches
            .build();
    }
}