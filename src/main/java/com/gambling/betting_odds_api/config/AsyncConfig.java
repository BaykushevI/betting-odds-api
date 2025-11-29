package com.gambling.betting_odds_api.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;

/**
 * Async configuration for non-blocking operations.
 * Enables @Async methods with proper thread pool and error handling.
 */
@Slf4j
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    /**
     * Configure thread pool for async operations.
     * 
     * Thread Pool Strategy:
     * - Core pool size: 5 threads (always alive)
     * - Max pool size: 10 threads (grows on demand)
     * - Queue capacity: 25 tasks (buffered before rejection)
     * 
     * Math Example:
     * - 5 threads busy → new tasks go to queue (up to 25)
     * - Queue full → new threads spawn (up to 10 total)
     * - All full → rejection policy triggers
     */
    @Bean(name = "taskExecutor")
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        
        // Core pool: Always-alive threads
        executor.setCorePoolSize(5);
        
        // Max pool: Maximum concurrent threads
        executor.setMaxPoolSize(10);
        
        // Queue capacity: Buffered tasks before scaling
        executor.setQueueCapacity(25);
        
        // Thread naming: Helps with debugging in logs
        executor.setThreadNamePrefix("Async-");
        
        // Graceful shutdown: Wait for tasks to complete
        executor.setWaitForTasksToCompleteOnShutdown(true);
        
        // Shutdown timeout: Max wait time (30 seconds)
        executor.setAwaitTerminationSeconds(30);
        
        // Initialize thread pool
        executor.initialize();
        
        log.info("Async thread pool configured: core={}, max={}, queue={}", 
                 executor.getCorePoolSize(), 
                 executor.getMaxPoolSize(), 
                 executor.getQueueCapacity());
        
        return executor;
    }

    /**
     * Handle uncaught exceptions in @Async methods.
     * Logs errors instead of silently failing.
     */
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new CustomAsyncExceptionHandler();
    }

    /**
     * Custom exception handler for async operations.
     * Prevents silent failures in background threads.
     */
    public static class CustomAsyncExceptionHandler implements AsyncUncaughtExceptionHandler {
        
        @Override
        public void handleUncaughtException(Throwable throwable, Method method, Object... params) {
            log.error("Async method '{}' threw exception: {}", 
                      method.getName(), 
                      throwable.getMessage(), 
                      throwable);
            
            // Log parameters for debugging
            if (params != null && params.length > 0) {
                log.error("Method parameters: {}", java.util.Arrays.toString(params));
            }
        }
    }
}