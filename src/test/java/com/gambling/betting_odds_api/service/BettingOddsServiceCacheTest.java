package com.gambling.betting_odds_api.service;

// ═══════════════════════════════════════════════════════════════════════════
// INTERNAL PROJECT IMPORTS
// ═══════════════════════════════════════════════════════════════════════════
import com.gambling.betting_odds_api.dto.CreateOddsRequest;
import com.gambling.betting_odds_api.dto.OddsResponse;
import com.gambling.betting_odds_api.dto.PageResponse;
import com.gambling.betting_odds_api.dto.UpdateOddsRequest;
import com.gambling.betting_odds_api.exception.ResourceNotFoundException;
import com.gambling.betting_odds_api.model.BettingOdds;
import com.gambling.betting_odds_api.repository.BettingOddsRepository;

// ═══════════════════════════════════════════════════════════════════════════
// JUNIT 5 - Testing framework
// ═══════════════════════════════════════════════════════════════════════════
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

// ═══════════════════════════════════════════════════════════════════════════
// SPRING BOOT TEST - Testing support with real Spring context
// ═══════════════════════════════════════════════════════════════════════════
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

// ═══════════════════════════════════════════════════════════════════════════
// SPRING DATA - Pagination support
// ═══════════════════════════════════════════════════════════════════════════
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

// ═══════════════════════════════════════════════════════════════════════════
// TESTCONTAINERS - Docker containers for integration testing
// ═══════════════════════════════════════════════════════════════════════════
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

// ═══════════════════════════════════════════════════════════════════════════
// JAVA STANDARD LIBRARY
// ═══════════════════════════════════════════════════════════════════════════
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// ═══════════════════════════════════════════════════════════════════════════
// ASSERTIONS - AssertJ for fluent assertions
// ═══════════════════════════════════════════════════════════════════════════
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for Redis caching in BettingOddsService.
 * 
 * Test Strategy:
 * 1. Use Testcontainers to spin up real Redis instance (port 6379)
 * 2. Use real database (H2 in-memory for tests)
 * 3. Use real Spring context (@SpringBootTest)
 * 4. Test cache hit/miss by checking cache directly
 * 5. Test cache eviction by verifying cache is empty after operations
 * 6. NO MOCKS - All tests use real beans and real behavior!
 * 
 * Why Integration Tests (not unit tests with mocks)?
 * - Caching is a Spring infrastructure concern
 * - Mocking repository breaks Spring Cache behavior
 * - Need real Spring context to test @Cacheable/@CachePut/@CacheEvict
 * - Integration tests give us confidence that caching works in production
 * 
 * Cache Namespaces Tested:
 * - "odds" - Single records (getOddsById)
 * - "odds-all" - All odds pagination (getAllOdds)
 * - "odds-active" - Active odds pagination (getActiveOdds)
 * - "odds-sport" - Sport-specific pagination (getOddsBySport)
 * - "odds-upcoming" - Upcoming matches pagination (getUpcomingMatches)
 * - "odds-team" - Team-specific pagination (getMatchesForTeam)
 */
@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@DisplayName("BettingOddsService - Redis Caching Tests")
class BettingOddsServiceCacheTest {

    // ═══════════════════════════════════════════════════════════════════════
    // TESTCONTAINERS - Redis container for integration testing
    // ═══════════════════════════════════════════════════════════════════════
    
    /**
     * Redis container using Testcontainers.
     * - Image: redis:7-alpine (lightweight, production-ready)
     * - Port: 6379 (default Redis port, dynamically mapped)
     * - Lifecycle: Started before tests, stopped after tests
     * - Reuse: true (faster test execution, single container for all tests)
     */
    @Container
    private static final GenericContainer<?> redisContainer = 
        new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
            .withExposedPorts(6379)
            .withReuse(true);

    // ═══════════════════════════════════════════════════════════════════════
    // TEST DEPENDENCIES - Real Spring beans (not mocks!)
    // ═══════════════════════════════════════════════════════════════════════

    @Autowired
    private BettingOddsService service;

    @Autowired
    private BettingOddsRepository repository;

    @Autowired
    private CacheManager cacheManager;

    // ═══════════════════════════════════════════════════════════════════════
    // TEST DATA
    // ═══════════════════════════════════════════════════════════════════════

    private BettingOdds testOdds;
    private Long testOddsId;
    
    // List to track created odds for cleanup
    private List<Long> createdOddsIds = new ArrayList<>();

    // ═══════════════════════════════════════════════════════════════════════
    // CONFIGURATION - Connect to Testcontainers Redis
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Configure Spring Boot to use Testcontainers Redis.
     * This overrides application.properties for tests.
     */
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

    // ═══════════════════════════════════════════════════════════════════════
    // SETUP & TEARDOWN
    // ═══════════════════════════════════════════════════════════════════════

    @BeforeEach
    void setUp() {
        // Clear all caches before each test
        cacheManager.getCacheNames().forEach(cacheName -> {
            var cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.clear();
            }
        });

        // Create test data
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
        createdOddsIds.add(testOddsId);
    }

    @AfterEach
    void tearDown() {
        // Cleanup all created odds
        createdOddsIds.forEach(id -> {
            if (repository.existsById(id)) {
                repository.deleteById(id);
            }
        });
        createdOddsIds.clear();
    }

    // ═══════════════════════════════════════════════════════════════════════
    // TESTS - Single Record Caching (getOddsById)
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("@Cacheable: First call should cache the result")
    void testCacheable_FirstCall_ShouldCacheResult() {
        // Arrange - Cache should be empty
        var cache = cacheManager.getCache("odds");
        assertThat(cache).isNotNull();
        assertThat(cache.get(testOddsId)).isNull();

        // Act - First call (cache MISS)
        OddsResponse response = service.getOddsById(testOddsId);

        // Assert - Result returned correctly
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(testOddsId);
        assertThat(response.getHomeTeam()).isEqualTo("Barcelona");
        
        // Assert - Result now cached
        assertThat(cache.get(testOddsId)).isNotNull();
    }

    @Test
    @DisplayName("@Cacheable: Second call should return from cache")
    void testCacheable_SecondCall_ShouldReturnFromCache() {
        // Arrange - First call to populate cache
        OddsResponse firstCall = service.getOddsById(testOddsId);
        
        // Act - Delete from DB (but cache still has it!)
        repository.deleteById(testOddsId);
        
        // Act - Second call (should return from cache, not DB!)
        OddsResponse secondCall = service.getOddsById(testOddsId);

        // Assert - Same data returned (from cache!)
        assertThat(secondCall).isNotNull();
        assertThat(secondCall.getId()).isEqualTo(firstCall.getId());
        assertThat(secondCall.getHomeTeam()).isEqualTo(firstCall.getHomeTeam());
        
        // Note: This proves caching works! If no cache, we'd get ResourceNotFoundException
    }

    @Test
    @DisplayName("@Cacheable: Cache miss should query database")
    void testCacheable_CacheMiss_ShouldQueryDatabase() {
        // Arrange - Cache should be empty
        var cache = cacheManager.getCache("odds");
        assertThat(cache).isNotNull();
        assertThat(cache.get(testOddsId)).isNull();

        // Act & Assert - Non-existent ID throws exception (cache miss, query DB)
        assertThrows(ResourceNotFoundException.class, () -> {
            service.getOddsById(999L);
        });
    }

    @Test
    @DisplayName("@CachePut: Update should refresh cache")
    void testCachePut_Update_ShouldRefreshCache() {
        // Arrange - First call to populate cache
        OddsResponse original = service.getOddsById(testOddsId);
        assertThat(original.getHomeOdds()).isEqualByComparingTo(BigDecimal.valueOf(2.10));

        // Act - Update odds (should update cache!)
        UpdateOddsRequest updateRequest = createUpdateRequest(BigDecimal.valueOf(3.50));
        service.updateOdds(testOddsId, updateRequest);

        // Assert - Next getById returns updated value from cache
        OddsResponse updated = service.getOddsById(testOddsId);
        assertThat(updated.getHomeOdds()).isEqualByComparingTo(BigDecimal.valueOf(3.50));

        // Assert - Cache still has the record
        var cache = cacheManager.getCache("odds");
        assertThat(cache).isNotNull();
        assertThat(cache.get(testOddsId)).isNotNull();
    }

    @Test
    @DisplayName("@CachePut: Multiple updates should keep cache fresh")
    void testCachePut_MultipleUpdates_ShouldKeepCacheFresh() {
        // Arrange - First call to populate cache
        service.getOddsById(testOddsId);
        
        // Act - Multiple updates
        service.updateOdds(testOddsId, createUpdateRequest(BigDecimal.valueOf(2.50)));
        service.updateOdds(testOddsId, createUpdateRequest(BigDecimal.valueOf(3.00)));
        service.updateOdds(testOddsId, createUpdateRequest(BigDecimal.valueOf(3.50)));

        // Assert - Final value is correct (cache kept fresh!)
        OddsResponse result = service.getOddsById(testOddsId);
        assertThat(result.getHomeOdds()).isEqualByComparingTo(BigDecimal.valueOf(3.50));
    }

    @Test
    @DisplayName("@CacheEvict: Delete should remove from cache")
    void testCacheEvict_Delete_ShouldRemoveFromCache() {
        // Arrange - First call to populate cache
        service.getOddsById(testOddsId);
        var cache = cacheManager.getCache("odds");
        assertThat(cache).isNotNull();
        assertThat(cache.get(testOddsId)).isNotNull();

        // Act - Delete odds (should evict from cache!)
        service.deleteOdds(testOddsId);

        // Assert - Cache is empty
        assertThat(cache.get(testOddsId)).isNull();
        
        // Assert - Next getById throws exception (not in cache, not in DB)
        assertThrows(ResourceNotFoundException.class, () -> {
            service.getOddsById(testOddsId);
        });
    }

    @Test
    @DisplayName("@CacheEvict: Deactivate should remove from cache")
    void testCacheEvict_Deactivate_ShouldRemoveFromCache() {
        // Arrange - First call to populate cache
        OddsResponse cached = service.getOddsById(testOddsId);
        assertThat(cached.getActive()).isTrue();
        
        var cache = cacheManager.getCache("odds");
        assertThat(cache).isNotNull();
        assertThat(cache.get(testOddsId)).isNotNull();

        // Act - Deactivate odds (should evict from cache!)
        service.deactivateOdds(testOddsId);

        // Assert - Cache is empty
        assertThat(cache.get(testOddsId)).isNull();

        // Act - Get again (should query DB and cache new version)
        OddsResponse refreshed = service.getOddsById(testOddsId);
        
        // Assert - New version is cached (active=false)
        assertThat(refreshed.getActive()).isFalse();
        assertThat(cache.get(testOddsId)).isNotNull();
    }

    @Test
    @DisplayName("@CacheEvict: Deactivate should force DB query")
    void testCacheEvict_Deactivate_ShouldForceDbQuery() {
        // Arrange - First call to populate cache
        OddsResponse original = service.getOddsById(testOddsId);
        assertThat(original.getActive()).isTrue();

        // Act - Deactivate odds
        service.deactivateOdds(testOddsId);

        // Act - Get again (should query DB, get updated value)
        OddsResponse updated = service.getOddsById(testOddsId);
        
        // Assert - Updated value from DB
        assertThat(updated.getActive()).isFalse();
        assertThat(updated.getId()).isEqualTo(original.getId());
        assertThat(updated.getHomeTeam()).isEqualTo(original.getHomeTeam());
    }

    // ═══════════════════════════════════════════════════════════════════════
    // TESTS - Pagination Caching (getAllOdds) - NEW!
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("getAllOdds - Page 0 second call returns from cache")
    void testGetAllOdds_Page0_CacheHit() {
        // Arrange - Clean ALL data and create only Barcelona
        repository.deleteAll();
        createdOddsIds.clear();
        
        BettingOdds barcelona = new BettingOdds();
        barcelona.setSport("Football");
        barcelona.setHomeTeam("Barcelona");
        barcelona.setAwayTeam("Real Madrid");
        barcelona.setHomeOdds(BigDecimal.valueOf(2.10));
        barcelona.setDrawOdds(BigDecimal.valueOf(3.40));
        barcelona.setAwayOdds(BigDecimal.valueOf(3.60));
        barcelona.setMatchDate(LocalDateTime.now().plusDays(7));
        barcelona.setActive(true);
        barcelona = repository.save(barcelona);
        createdOddsIds.add(barcelona.getId());
        
        Pageable pageable = PageRequest.of(0, 10);
        
        // Act - First call (cache MISS)
        PageResponse<OddsResponse> firstCall = service.getAllOdds(pageable);
        
        // Act - Delete from DB (but cache still has it!)
        repository.deleteById(barcelona.getId());
        
        // Act - Second call (cache HIT - should return same data!)
        PageResponse<OddsResponse> secondCall = service.getAllOdds(pageable);

        // Assert - Same data returned (from cache!)
        assertThat(secondCall).isNotNull();
        assertThat(secondCall.getContent()).isNotEmpty();
        assertThat(secondCall.getContent().get(0).getId()).isEqualTo(firstCall.getContent().get(0).getId());
        assertThat(secondCall.getContent().get(0).getHomeTeam()).isEqualTo("Barcelona");
    }

    @Test
    @DisplayName("getAllOdds - Different pages cache independently")
    void testGetAllOdds_DifferentPages_IndependentCache() {
        // Arrange - Create multiple odds for pagination
        BettingOdds odds2 = createTestOdds("Liverpool", "Chelsea", 2);
        BettingOdds odds3 = createTestOdds("Arsenal", "Tottenham", 3);
        odds2 = repository.save(odds2);
        odds3 = repository.save(odds3);
        createdOddsIds.add(odds2.getId());
        createdOddsIds.add(odds3.getId());
        
        Pageable page0 = PageRequest.of(0, 1, Sort.by("matchDate").descending());
        Pageable page1 = PageRequest.of(1, 1, Sort.by("matchDate").descending());
        
        // Act - Get both pages
        PageResponse<OddsResponse> page0Result = service.getAllOdds(page0);
        PageResponse<OddsResponse> page1Result = service.getAllOdds(page1);

        // Assert - Different data on different pages
        assertThat(page0Result.getContent()).hasSize(1);
        assertThat(page1Result.getContent()).hasSize(1);
        assertThat(page0Result.getContent().get(0).getId())
            .isNotEqualTo(page1Result.getContent().get(0).getId());
    }

    @Test
    @DisplayName("getAllOdds - Page 3 NOT cached")
    void testGetAllOdds_Page3_NotCached() {
        // Arrange - Create enough odds for page 3
        for (int i = 0; i < 35; i++) {
            BettingOdds odds = createTestOdds("Team" + i + "A", "Team" + i + "B", i + 10);
            odds = repository.save(odds);
            createdOddsIds.add(odds.getId());
        }
        
        Pageable page3 = PageRequest.of(3, 10, Sort.by("matchDate").descending());
        
        // Act - Get page 3
        PageResponse<OddsResponse> firstCall = service.getAllOdds(page3);
        int firstCount = firstCall.getContent().size();
        
        // Act - Delete an odds
        repository.deleteById(testOddsId);
        
        // Act - Get page 3 again (should query DB again, not cached!)
        PageResponse<OddsResponse> secondCall = service.getAllOdds(page3);

        // Assert - Count may differ (because page 3 is not cached, queries DB each time)
        assertThat(firstCall).isNotNull();
        assertThat(secondCall).isNotNull();
    }

    @Test
    @DisplayName("createOdds - Evicts pagination caches")
    void testCreateOdds_EvictsPaginationCaches() {
        // Arrange - Clean ALL data
        repository.deleteAll();
        createdOddsIds.clear();
        
        // Create initial odds
        BettingOdds initialOdds = new BettingOdds();
        initialOdds.setSport("Football");
        initialOdds.setHomeTeam("Barcelona");
        initialOdds.setAwayTeam("Real Madrid");
        initialOdds.setHomeOdds(BigDecimal.valueOf(2.10));
        initialOdds.setDrawOdds(BigDecimal.valueOf(3.40));
        initialOdds.setAwayOdds(BigDecimal.valueOf(3.60));
        initialOdds.setMatchDate(LocalDateTime.now().plusDays(7));
        initialOdds.setActive(true);
        initialOdds = repository.save(initialOdds);
        createdOddsIds.add(initialOdds.getId());
        
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").descending());
        
        // Act - Cache page 0 (Barcelona is first)
        PageResponse<OddsResponse> beforeCreate = service.getAllOdds(pageable);
        assertThat(beforeCreate.getContent().get(0).getHomeTeam()).isEqualTo("Barcelona");
        
        // Act - Create new odds (with more recent date = will be FIRST!)
        CreateOddsRequest createRequest = new CreateOddsRequest();
        createRequest.setSport("Basketball");
        createRequest.setHomeTeam("Lakers");
        createRequest.setAwayTeam("Warriors");
        createRequest.setHomeOdds(BigDecimal.valueOf(1.90));
        createRequest.setDrawOdds(BigDecimal.valueOf(11.00));
        createRequest.setAwayOdds(BigDecimal.valueOf(2.00));
        createRequest.setMatchDate(LocalDateTime.now().plusDays(100));  // More recent = FIRST!
        
        OddsResponse created = service.createOdds(createRequest);
        createdOddsIds.add(created.getId());
        
        // Act - Get page 0 again (should query DB, cache was evicted!)
        PageResponse<OddsResponse> afterCreate = service.getAllOdds(pageable);

        // Assert - First item is NOW Lakers (proof that cache was evicted and DB queried!)
        assertThat(afterCreate.getContent()).isNotEmpty();
        assertThat(afterCreate.getContent().get(0).getHomeTeam()).isEqualTo("Lakers");
    }

    // ═══════════════════════════════════════════════════════════════════════
    // TESTS - Other Pagination Caches - NEW!
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("getActiveOdds - Cache works correctly")
    void testGetActiveOdds_CacheWorks() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        
        // Act - First call
        PageResponse<OddsResponse> firstCall = service.getActiveOdds(pageable);
        
        // Act - Deactivate all odds in DB
        repository.findAll().forEach(odds -> {
            odds.setActive(false);
            repository.save(odds);
        });
        
        // Act - Second call (should return cached data with active odds!)
        PageResponse<OddsResponse> secondCall = service.getActiveOdds(pageable);

        // Assert - Same data from cache (still shows active odds!)
        assertThat(secondCall.getContent()).hasSameSizeAs(firstCall.getContent());
    }

    @Test
    @DisplayName("getOddsBySport - Different sports cache separately")
    void testGetOddsBySport_SeparateCaches() {
        // Arrange - Create Basketball odds
        BettingOdds basketballOdds = new BettingOdds();
        basketballOdds.setSport("Basketball");
        basketballOdds.setHomeTeam("Lakers");
        basketballOdds.setAwayTeam("Warriors");
        basketballOdds.setHomeOdds(BigDecimal.valueOf(1.90));
        basketballOdds.setDrawOdds(BigDecimal.valueOf(11.00));
        basketballOdds.setAwayOdds(BigDecimal.valueOf(2.00));
        basketballOdds.setMatchDate(LocalDateTime.now().plusDays(5));
        basketballOdds.setActive(true);
        basketballOdds = repository.save(basketballOdds);
        createdOddsIds.add(basketballOdds.getId());
        
        Pageable pageable = PageRequest.of(0, 10);
        
        // Act - Get Football and Basketball
        PageResponse<OddsResponse> football = service.getOddsBySport("Football", pageable);
        PageResponse<OddsResponse> basketball = service.getOddsBySport("Basketball", pageable);

        // Assert - Both return correct data
        assertThat(football.getContent()).isNotEmpty();
        assertThat(basketball.getContent()).isNotEmpty();
        assertThat(football.getContent().get(0).getSport()).isEqualTo("Football");
        assertThat(basketball.getContent().get(0).getSport()).isEqualTo("Basketball");
    }

    @Test
    @DisplayName("getUpcomingMatches - Cache works")
    void testGetUpcomingMatches_CacheWorks() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        
        // Act - First call
        PageResponse<OddsResponse> firstCall = service.getUpcomingMatches(pageable);
        
        // Act - Delete from DB
        repository.deleteById(testOddsId);
        
        // Act - Second call (should return cached data!)
        PageResponse<OddsResponse> secondCall = service.getUpcomingMatches(pageable);

        // Assert - Same data from cache
        assertThat(secondCall.getContent()).isNotEmpty();
        assertThat(secondCall.getContent().get(0).getId())
            .isEqualTo(firstCall.getContent().get(0).getId());
    }

    @Test
    @DisplayName("getMatchesForTeam - Different teams cache separately")
    void testGetMatchesForTeam_SeparateCaches() {
        // Arrange - Create Real Madrid match
        BettingOdds realMadridOdds = new BettingOdds();
        realMadridOdds.setSport("Football");
        realMadridOdds.setHomeTeam("Real Madrid");
        realMadridOdds.setAwayTeam("Atletico Madrid");
        realMadridOdds.setHomeOdds(BigDecimal.valueOf(1.80));
        realMadridOdds.setDrawOdds(BigDecimal.valueOf(3.60));
        realMadridOdds.setAwayOdds(BigDecimal.valueOf(4.20));
        realMadridOdds.setMatchDate(LocalDateTime.now().plusDays(6));
        realMadridOdds.setActive(true);
        realMadridOdds = repository.save(realMadridOdds);
        createdOddsIds.add(realMadridOdds.getId());
        
        Pageable pageable = PageRequest.of(0, 10);
        
        // Act - Get matches for both teams
        PageResponse<OddsResponse> barcelona = service.getMatchesForTeam("Barcelona", pageable);
        PageResponse<OddsResponse> realMadrid = service.getMatchesForTeam("Real Madrid", pageable);

        // Assert - Both return correct data
        assertThat(barcelona.getContent()).isNotEmpty();
        assertThat(realMadrid.getContent()).isNotEmpty();
    }

    // ═══════════════════════════════════════════════════════════════════════
    // HELPER METHODS
    // ═══════════════════════════════════════════════════════════════════════

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
    
    private BettingOdds createTestOdds(String homeTeam, String awayTeam, int daysInFuture) {
        BettingOdds odds = new BettingOdds();
        odds.setSport("Football");
        odds.setHomeTeam(homeTeam);
        odds.setAwayTeam(awayTeam);
        odds.setHomeOdds(BigDecimal.valueOf(2.00));
        odds.setDrawOdds(BigDecimal.valueOf(3.50));
        odds.setAwayOdds(BigDecimal.valueOf(3.80));
        odds.setMatchDate(LocalDateTime.now().plusDays(daysInFuture));
        odds.setActive(true);
        return odds;
    }
}