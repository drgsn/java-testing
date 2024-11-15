package com.itadviser.config;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Base class for integration tests that provides a real PostgreSQL database
 * using TestContainers.
 *
 * Key Annotations:
 * @Testcontainers - Enables TestContainers JUnit 5 integration:
 *   - Manages container lifecycle
 *   - Starts containers before tests
 *   - Stops containers after tests
 */
@Testcontainers
public abstract class AbstractIntegrationTest {

    /**
     * @Container annotation marks this as a TestContainer
     * Static container is shared between test classes for efficiency
     * Configures PostgreSQL 17.0:
     * - Creates database named "testdb"
     * - Sets up test user credentials
     */
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17.0")
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test");

    /**
     * @DynamicPropertySource allows dynamic configuration of Spring properties
     * This method:
     * - Provides database connection details to Spring
     * - Uses actual container connection information
     * - Automatically updates if container port changes
     */
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
}