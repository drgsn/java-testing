package com.itadviser.incremental;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.itadviser.config.AbstractIntegrationTest;
import com.itadviser.UserAlreadyExistsException;
import com.itadviser.UserDto;
import com.itadviser.UserResponse;
import com.itadviser.UserService;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;


/**
 * Integration test for UserService that tests the service layer with a real database
 * and full Spring application context.
 *
 * Key Annotations:
 * @SpringBootTest - Provides full Spring application context:
 *   - Loads complete Spring configuration
 *   - Creates all beans in the application context
 *   - More comprehensive than @DataJpaTest but slower
 *   - Suitable for testing service layer with all its dependencies
 *
 * @ActiveProfiles("test") - Activates the "test" Spring profile:
 *   - Loads test-specific configurations
 *   - Can override default properties for testing
 *   - Helps isolate test environment from production
 */
@SpringBootTest
@ActiveProfiles("test")
class UserServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private UserService userService;

    /**
     * Tests the complete flow of creating and retrieving a user.
     *
     * Test demonstrates:
     * - Integration between Service and Repository layers
     * - DTO to Entity conversion
     * - Entity to Response conversion
     * - Transaction management
     * - Database persistence
     *
     * @Order(1) ensures this test runs before the duplicate user test
     */
    @Test
    @Order(1)
    void shouldCreateAndRetrieveUser() {
        // given
        UserDto userDto = new UserDto("test@example.com", "Test User");

        // when
        UserResponse createdUser = userService.createUser(userDto);
        UserResponse retrievedUser = userService.getUserById(createdUser.getId());

        // then
        assertThat(retrievedUser).isNotNull();
        assertThat(retrievedUser.getEmail()).isEqualTo(userDto.getEmail());
        assertThat(retrievedUser.getName()).isEqualTo(userDto.getName());
    }

    /**
     * Tests the duplicate user validation logic.
     *
     * Test demonstrates:
     * - Business rule validation
     * - Exception handling
     * - Database constraint enforcement
     * - Transaction rollback on failure
     *
     * @Order(2) ensures this runs after the user creation test,
     * testing against an existing user
     */
    @Test
    @Order(2)
    void shouldThrowExceptionWhenCreatingDuplicateUser() {
        // given
        UserDto userDto = new UserDto("test@example.com", "Test User");

        // when & then
        assertThrows(UserAlreadyExistsException.class, () ->
            userService.createUser(userDto)
        );
    }
}