package com.gambling.betting_odds_api.config;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;

import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test async configuration and thread pool behavior.
 */
@Slf4j
@SpringBootTest
class AsyncConfigTest {

    @Autowired
    private AsyncTestService asyncTestService;

    @Test
    void testAsyncMethodExecutesInDifferentThread() throws Exception {
        // Get main thread name
        String mainThread = Thread.currentThread().getName();
        log.info("Main thread: {}", mainThread);

        // Call async method
        CompletableFuture<String> result = asyncTestService.asyncMethod();
        
        // Async method runs in background - main thread continues
        log.info("Async method called, main thread continues...");

        // Wait for async result
        String asyncThread = result.get();
        log.info("Async thread: {}", asyncThread);

        // Verify different threads
        assertThat(asyncThread).startsWith("Async-");
        assertThat(asyncThread).isNotEqualTo(mainThread);
    }

    /**
     * Test configuration to register AsyncTestService bean.
     * This makes the service available for @Autowired injection.
     */
    @TestConfiguration
    static class AsyncTestConfiguration {
        
        @Bean
        public AsyncTestService asyncTestService() {
            return new AsyncTestService();
        }
    }

    /**
     * Test service with @Async method.
     * Now registered as a Spring bean via @TestConfiguration.
     */
    @Slf4j
    static class AsyncTestService {

        @Async("taskExecutor")
        public CompletableFuture<String> asyncMethod() {
            String threadName = Thread.currentThread().getName();
            log.info("Executing async method in thread: {}", threadName);
            
            // Simulate work
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            return CompletableFuture.completedFuture(threadName);
        }
    }
}