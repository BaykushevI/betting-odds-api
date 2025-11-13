package com.gambling.betting_odds_api.service;

import com.gambling.betting_odds_api.dto.OddsResponse;
import com.gambling.betting_odds_api.dto.UpdateOddsRequest;
import com.gambling.betting_odds_api.exception.ResourceNotFoundException;
import com.gambling.betting_odds_api.model.BettingOdds;
import com.gambling.betting_odds_api.repository.BettingOddsRepository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * BettingOddsServiceCacheTest - Tests for Redis caching behavior using Testcontainers.
 */
@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@DisplayName("BettingOddsService - Redis Caching Tests")
class BettingOddsServiceCacheTest {

    @Container
    private static final GenericContainer<?> redisContainer = 
        new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
            .withExposedPorts(6379)
            .withReuse(true);

    @Autowired
    private BettingOddsService service;

    @Autowired
    private BettingOddsRepository repository;

    @Autowired
    private CacheManager cacheManager;

    private BettingOdds testOdds;
    private Long testOddsId;

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redisContainer::getHost);
        registry.add("spring.data.redis.port", 
            () -> redisContainer.getMappedPort(6379).toString());
    }

    @BeforeAll
    static void beforeAll() {
        assertThat(redisContainer.isRunning()).isTrue();
    }

    @BeforeEach
    void setUp() {
        cacheManager.getCacheNames().forEach(cacheName -> {
            var cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.clear();
            }
        });

        testOdds = new BettingOdds();
        testOdds.setSport("Football");
        testOdds.setHomeTeam("Barcelona");
        testOdds.setAwayTeam("Real Madrid");
        testOdds.setHomeOdds(BigDecimal.valueOf(2.10));
        testOdds.setDrawOdds(BigDecimal.valueOf(3.40));
        testOdds.setAwayOdds(BigDecimal.valueOf(3.60));
        testOdds.setMatchDate(LocalDateTime.now().plusDays(7));
        testOdds.setActive(true);

        testOdds = repository.save(testOdds);
        testOddsId = testOdds.getId();
    }

    @AfterEach
    void tearDown() {
        if (testOddsId != null && repository.existsById(testOddsId)) {
            repository.deleteById(testOddsId);
        }
    }

    @Test
    @DisplayName("@Cacheable: First call should cache the result")
    void testCacheable_FirstCall_ShouldCacheResult() {
        var cache = cacheManager.getCache("odds");
        assertThat(cache).isNotNull();
        assertThat(cache.get(testOddsId)).isNull();

        OddsResponse response = service.getOddsById(testOddsId);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(testOddsId);
        assertThat(response.getHomeTeam()).isEqualTo("Barcelona");
        assertThat(cache.get(testOddsId)).isNotNull();
    }

    @Test
    @DisplayName("@Cacheable: Second call should return from cache")
    void testCacheable_SecondCall_ShouldReturnFromCache() {
        OddsResponse firstCall = service.getOddsById(testOddsId);
        repository.deleteById(testOddsId);
        OddsResponse secondCall = service.getOddsById(testOddsId);

        assertThat(secondCall).isNotNull();
        assertThat(secondCall.getId()).isEqualTo(firstCall.getId());
        assertThat(secondCall.getHomeTeam()).isEqualTo(firstCall.getHomeTeam());
    }

    @Test
    @DisplayName("@Cacheable: Cache miss should query database")
    void testCacheable_CacheMiss_ShouldQueryDatabase() {
        var cache = cacheManager.getCache("odds");
        assertThat(cache).isNotNull();
        assertThat(cache.get(testOddsId)).isNull();

        assertThrows(ResourceNotFoundException.class, () -> {
            service.getOddsById(999L);
        });
    }

    @Test
    @DisplayName("@CachePut: Update should refresh cache")
    void testCachePut_Update_ShouldRefreshCache() {
        OddsResponse original = service.getOddsById(testOddsId);
        assertThat(original.getHomeOdds()).isEqualByComparingTo(BigDecimal.valueOf(2.10));

        UpdateOddsRequest updateRequest = createUpdateRequest(BigDecimal.valueOf(3.50));
        service.updateOdds(testOddsId, updateRequest);

        OddsResponse updated = service.getOddsById(testOddsId);
        assertThat(updated.getHomeOdds()).isEqualByComparingTo(BigDecimal.valueOf(3.50));

        var cache = cacheManager.getCache("odds");
        assertThat(cache).isNotNull();
        assertThat(cache.get(testOddsId)).isNotNull();
    }

    @Test
    @DisplayName("@CachePut: Multiple updates should keep cache fresh")
    void testCachePut_MultipleUpdates_ShouldKeepCacheFresh() {
        service.getOddsById(testOddsId);
        service.updateOdds(testOddsId, createUpdateRequest(BigDecimal.valueOf(2.50)));
        service.updateOdds(testOddsId, createUpdateRequest(BigDecimal.valueOf(3.00)));
        service.updateOdds(testOddsId, createUpdateRequest(BigDecimal.valueOf(3.50)));

        OddsResponse result = service.getOddsById(testOddsId);
        assertThat(result.getHomeOdds()).isEqualByComparingTo(BigDecimal.valueOf(3.50));
    }

    @Test
    @DisplayName("@CacheEvict: Delete should remove from cache")
    void testCacheEvict_Delete_ShouldRemoveFromCache() {
        service.getOddsById(testOddsId);
        var cache = cacheManager.getCache("odds");
        assertThat(cache).isNotNull();
        assertThat(cache.get(testOddsId)).isNotNull();

        service.deleteOdds(testOddsId);

        assertThat(cache.get(testOddsId)).isNull();
        assertThrows(ResourceNotFoundException.class, () -> {
            service.getOddsById(testOddsId);
        });
    }

    @Test
    @DisplayName("@CacheEvict: Deactivate should remove from cache")
    void testCacheEvict_Deactivate_ShouldRemoveFromCache() {
        OddsResponse cached = service.getOddsById(testOddsId);
        assertThat(cached.getActive()).isTrue();
        
        var cache = cacheManager.getCache("odds");
        assertThat(cache).isNotNull();
        assertThat(cache.get(testOddsId)).isNotNull();

        service.deactivateOdds(testOddsId);

        assertThat(cache.get(testOddsId)).isNull();

        OddsResponse refreshed = service.getOddsById(testOddsId);
        assertThat(refreshed.getActive()).isFalse();
        assertThat(cache.get(testOddsId)).isNotNull();
    }

    @Test
    @DisplayName("@CacheEvict: Deactivate should force DB query")
    void testCacheEvict_Deactivate_ShouldForceDbQuery() {
        OddsResponse original = service.getOddsById(testOddsId);
        assertThat(original.getActive()).isTrue();

        service.deactivateOdds(testOddsId);

        OddsResponse updated = service.getOddsById(testOddsId);
        assertThat(updated.getActive()).isFalse();
        assertThat(updated.getId()).isEqualTo(original.getId());
        assertThat(updated.getHomeTeam()).isEqualTo(original.getHomeTeam());
    }

    private UpdateOddsRequest createUpdateRequest(BigDecimal homeOdds) {
        UpdateOddsRequest request = new UpdateOddsRequest();
        request.setSport("Football");
        request.setHomeTeam("Barcelona");
        request.setAwayTeam("Real Madrid");
        request.setHomeOdds(homeOdds);
        request.setDrawOdds(BigDecimal.valueOf(3.40));
        request.setAwayOdds(BigDecimal.valueOf(3.60));
        request.setMatchDate(LocalDateTime.now().plusDays(7));
        request.setActive(true);
        return request;
    }
}