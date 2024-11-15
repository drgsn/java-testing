package com.itadviser.incremental;

import com.itadviser.config.AbstractIntegrationTest;
import com.itadviser.User;
import com.itadviser.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test class for UserRepository that demonstrates Spring Data JPA repository testing
 * with TestContainers for database integration tests.
 *
 * Key Annotations:
 * @DataJpaTest - Configures test slice for JPA repositories:
 *   - Enables JPA repositories
 *   - Configures in-memory database (if not overridden)
 *   - Sets up Spring Data JPA
 *   - Enables transaction management
 *   - Loads only JPA-related beans (not full application context)
 *
 * @AutoConfigureTestDatabase - Controls test database configuration:
 *   - Replace.NONE tells Spring not to replace the database connection
 *   - This allows us to use TestContainers instead of H2/in-memory database
 *
 * @ActiveProfiles("test") - Activates the "test" Spring profile:
 *   - Loads test-specific configuration properties
 *   - Helps separate test and production configurations
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class UserRepositoryIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    /**
     * Tests the repository's ability to save a user and find it by email.
     *
     * Test Structure:
     * 1. Given (Arrange) - Create test user data
     * 2. When (Act) - Perform repository operations
     * 3. Then (Assert) - Verify results
     *
     * Each test method runs in its own transaction and is rolled back after completion,
     * ensuring test isolation.
     */
    @Test
    void shouldSaveAndFindUserByEmail() {
        // given
        User user = new User("test@example.com", "Test User");

        // when
        User savedUser = userRepository.save(user);
        User foundUser = userRepository.findByEmail("test@example.com").orElse(null);

        // then
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getEmail()).isEqualTo(savedUser.getEmail());
        assertThat(foundUser.getName()).isEqualTo(savedUser.getName());
    }

    /**
     * Tests the existsByEmail method of the repository.
     *
     * This test demonstrates:
     * - Testing custom query methods
     * - Boolean return values from repository methods
     * - Persistence verification
     */
    @Test
    void shouldCheckIfUserExists() {
        // given
        User user = new User("test@example.com", "Test User");
        userRepository.save(user);

        // when
        boolean exists = userRepository.existsByEmail("test@example.com");

        // then
        assertThat(exists).isTrue();
    }
}