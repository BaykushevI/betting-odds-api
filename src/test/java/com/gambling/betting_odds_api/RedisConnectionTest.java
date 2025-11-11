package com.gambling.betting_odds_api;

// JUnit 5 - Testing framework
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

// Spring Boot Test
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;

// Assertions
import static org.junit.jupiter.api.Assertions.*;

/**
 * RedisConnectionTest - Verify Redis connection and basic operations.
 * 
 * What we test:
 *   1. Redis connection works
 *   2. Can store data (set operation)
 *   3. Can retrieve data (get operation)
 *   4. Can delete data (delete operation)
 * 
 * Note: This test requires Redis to be running!
 * Start Redis: docker start redis-betting
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Redis Connection Tests")
public class RedisConnectionTest {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    @DisplayName("Redis connection should work")
    void testRedisConnection() {
        // ARRANGE
        String key = "test:connection";
        String value = "Hello Redis!";
        
        // ACT - Store value in Redis
        redisTemplate.opsForValue().set(key, value);
        
        // ASSERT - Retrieve and verify
        String retrieved = (String) redisTemplate.opsForValue().get(key);
        
        assertNotNull(retrieved, "Retrieved value should not be null");
        assertEquals(value, retrieved, "Retrieved value should match stored value");
        
        // CLEANUP - Delete test key
        redisTemplate.delete(key);
    }
    
    @Test
    @DisplayName("Should store and retrieve complex object")
    void testStoreComplexObject() {
        // ARRANGE
        String key = "test:object";
        TestData testData = new TestData("John", 30, "Developer");
        
        // ACT
        redisTemplate.opsForValue().set(key, testData);
        TestData retrieved = (TestData) redisTemplate.opsForValue().get(key);
        
        // ASSERT
        assertNotNull(retrieved);
        assertEquals(testData.name, retrieved.name);
        assertEquals(testData.age, retrieved.age);
        assertEquals(testData.role, retrieved.role);
        
        // CLEANUP
        redisTemplate.delete(key);
    }
    
    @Test
    @DisplayName("Should handle key deletion")
    void testDeleteKey() {
        // ARRANGE
        String key = "test:delete";
        redisTemplate.opsForValue().set(key, "Delete me!");
        
        // ACT
        Boolean deleted = redisTemplate.delete(key);
        Object retrieved = redisTemplate.opsForValue().get(key);
        
        // ASSERT
        assertTrue(deleted, "Delete operation should return true");
        assertNull(retrieved, "Key should not exist after deletion");
    }
    
    // Simple test data class
    static class TestData {
        public String name;
        public int age;
        public String role;
        
        public TestData() {} // Required for deserialization
        
        public TestData(String name, int age, String role) {
            this.name = name;
            this.age = age;
            this.role = role;
        }
    }
}