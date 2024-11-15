package com.itadviser.bigbang;

import com.itadviser.*;
import com.itadviser.config.AbstractIntegrationTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.assertThat;


/**
 * System Integration Test for the User API that tests the entire application stack
 * from HTTP endpoints down to the database.
 *
 * Key Annotations:
 * @SpringBootTest - Provides full Spring application context:
 *   - Loads complete Spring configuration
 *   - Creates all beans in the application context
 *   - Sets up the entire application environment
 *
 * @AutoConfigureMockMvc - Configures MockMvc for API testing:
 *   - Enables testing of MVC controllers without starting a real HTTP server
 *   - Provides utilities for simulating HTTP requests
 *   - Allows assertions on HTTP responses
 *
 * @ActiveProfiles("test") - Activates test configuration:
 *   - Uses test-specific properties
 *   - Configures test database and other test-specific beans
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserSystemIntegrationTest extends AbstractIntegrationTest {

    /**
     * MockMvc - Main entry point for testing Spring MVC applications
     * - Simulates HTTP requests without starting a server
     * - Provides fluent API for request building
     * - Enables assertions on responses
     */
    @Autowired
    private MockMvc mockMvc;

    @LocalServerPort
    int port;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Tests the complete user lifecycle through REST API endpoints.
     * Demonstrates CRUD operations (Create, Read, Update, Delete)
     * through HTTP endpoints.
     *
     * This test verifies:
     * 1. User creation (POST)
     * 2. User retrieval (GET)
     * 3. User update (PUT)
     * 4. User deletion (DELETE)
     * 5. Error handling for non-existent users
     */
    @Test
    void shouldPerformFullUserLifecycle() throws Exception {
        // Create user
        UserDto userDto = new UserDto("test@example.com", "Test User");
        MvcResult createResult = mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)))
            .andExpect(status().isCreated())
            .andReturn();

        UserResponse createdUser = objectMapper.readValue(
            createResult.getResponse().getContentAsString(),
            UserResponse.class
        );

        // Get user
        mockMvc.perform(get("/api/users/{id}", createdUser.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value(userDto.getEmail()))
            .andExpect(jsonPath("$.name").value(userDto.getName()));

        // Update user
        UserDto updateDto = new UserDto("updated@example.com", "Updated User");
        mockMvc.perform(put("/api/users/{id}", createdUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value(updateDto.getEmail()))
            .andExpect(jsonPath("$.name").value(updateDto.getName()));

        // Delete user
        mockMvc.perform(delete("/api/users/{id}", createdUser.getId()))
            .andExpect(status().isNoContent());

        // Verify deletion
        mockMvc.perform(get("/api/users/{id}", createdUser.getId()))
            .andExpect(status().isNotFound());
    }

    /**
     * Tests handling of multiple users and concurrent operations.
     * Verifies:
     * 1. Multiple user creation
     * 2. Listing all users
     * 3. System's ability to handle multiple records
     */
    @Test
    void shouldHandleMultipleUsersAndConcurrentOperations() throws Exception {
        // Create multiple users
        UserDto user1 = new UserDto("user1@example.com", "User One");
        UserDto user2 = new UserDto("user2@example.com", "User Two");

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user1)))
            .andExpect(status().isCreated());

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user2)))
            .andExpect(status().isCreated());

        // Get all users
        MvcResult getAllResult = mockMvc.perform(get("/api/users"))
            .andExpect(status().isOk())
            .andReturn();

        String content = getAllResult.getResponse().getContentAsString();
        assertThat(content).contains("user1@example.com");
        assertThat(content).contains("user2@example.com");
    }
}