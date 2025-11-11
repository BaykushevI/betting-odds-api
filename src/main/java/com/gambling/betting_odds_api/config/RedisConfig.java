package com.gambling.betting_odds_api.config;

// Spring Framework - Redis and caching
import org.springframework.cache.annotation.EnableCaching;      // Enable caching support
import org.springframework.context.annotation.Bean;             // Define Spring beans
import org.springframework.context.annotation.Configuration;    // Mark as configuration class
import org.springframework.data.redis.connection.RedisConnectionFactory; // Redis connection
import org.springframework.data.redis.core.RedisTemplate;       // Redis operations template
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer; // JSON serialization
import org.springframework.data.redis.serializer.StringRedisSerializer;  // String serialization

/**
 * RedisConfig - Configuration for Redis caching.
 * 
 * Purpose:
 *   - Configure Redis connection
 *   - Define RedisTemplate for Redis operations
 *   - Configure serialization (how data is stored in Redis)
 *   - Enable caching annotations (@Cacheable, @CacheEvict, etc.)
 * 
 * Redis Data Storage Format:
 *   - Keys: String (e.g., "odds::123")
 *   - Values: JSON (e.g., {"id":123,"sport":"Football",...})
 * 
 * Connection:
 *   - Spring Boot auto-configures connection using application.properties
 *   - RedisConnectionFactory is auto-created by Spring Boot
 *   - We just need to configure RedisTemplate
 * 
 * Why RedisTemplate?
 *   - High-level abstraction for Redis operations
 *   - Type-safe operations (opsForValue, opsForHash, opsForList, etc.)
 *   - Handles serialization/deserialization automatically
 *   - Connection pooling and error handling built-in
 */
@Configuration
@EnableCaching  // Enable Spring's annotation-driven cache management
public class RedisConfig {

    /**
     * Configure RedisTemplate for Redis operations.
     * 
     * RedisTemplate is the main interface for interacting with Redis.
     * We configure how keys and values are serialized:
     *   - Keys: String serializer (simple text)
     *   - Values: JSON serializer (convert Java objects to JSON)
     * 
     * Why JSON serialization?
     *   - Human-readable (can inspect in Redis CLI)
     *   - Language-agnostic (can be read by other apps)
     *   - Supports complex objects (OddsResponse, etc.)
     * 
     * Alternative: JdkSerializationRedisSerializer
     *   - Faster but binary format (not human-readable)
     *   - Tied to Java (can't read from other languages)
     * 
     * @param connectionFactory Auto-injected by Spring Boot
     * @return Configured RedisTemplate
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(
            RedisConnectionFactory connectionFactory) {
        
        // Create RedisTemplate instance
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        
        // Set connection factory (provided by Spring Boot auto-configuration)
        template.setConnectionFactory(connectionFactory);
        
        // Configure key serializer: String
        // Keys will be stored as plain text: "odds::123"
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);
        
        // Configure value serializer: JSON
        // Values will be stored as JSON: {"id":123,"sport":"Football",...}
        GenericJackson2JsonRedisSerializer jsonSerializer = 
            new GenericJackson2JsonRedisSerializer();
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);
        
        // Initialize template
        template.afterPropertiesSet();
        
        return template;
    }
}