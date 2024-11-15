package com.itadviser.unittest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.itadviser.User;
import com.itadviser.UserAlreadyExistsException;
import com.itadviser.UserDto;
import com.itadviser.UserNotFoundException;
import com.itadviser.UserRepository;
import com.itadviser.UserResponse;
import com.itadviser.UserService;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit test suite for UserService using Mockito framework.
 * Tests service layer in isolation by mocking dependencies.
 *
 * Key Annotations:
 * @ExtendWith(MockitoExtension.class) - Enables Mockito support:
 *   - Initializes mocks
 *   - Handles mock lifecycle
 *   - Provides mock injection
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    /**
     * @Mock creates a mock UserRepository:
     * - Simulates repository behavior
     * - Allows controlling method returns
     * - Enables verification of interactions
     */
    @Mock
    private UserRepository userRepository;

    /**
     * @Spy @InjectMocks on UserService:
     * - Creates a real UserService instance
     * - Injects mocked UserRepository
     * - Allows partial mocking if needed
     */
    @Spy
    @InjectMocks
    private UserService userService;

    /**
     * Test data objects used across multiple tests
     */
    private User testUser;
    private UserDto testUserDto;

    /**
     * Sets up test data before each test
     * - Creates fresh test objects
     * - Avoids test interference
     * - Ensures consistent test state
     */
    @BeforeEach
    void setUp() {
        testUser = new User("test@example.com", "Test User");
        testUser.setId(1L);
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());

        testUserDto = new UserDto("test@example.com", "Test User");
    }

    /**
     * Tests successful user creation scenario
     * Verifies:
     * - Email availability check
     * - User saving
     * - Response mapping
     * - Repository interaction count
     */
    @Test
    void createUser_WhenEmailNotTaken_ShouldCreateAndReturnUser() {
        // Arrange - Setup mock behavior
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act - Call method under test
        UserResponse response = userService.createUser(testUserDto);

        // Assert - Verify results
        assertNotNull(response);
        assertEquals(testUser.getId(), response.getId());
        assertEquals(testUser.getEmail(), response.getEmail());

        // Verify - Check repository interactions
        verify(userRepository, times(1)).existsByEmail(testUserDto.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    /**
     * Tests duplicate email handling
     * Verifies:
     * - Exception throwing
     * - No save attempt made
     * - Correct repository interactions
     */
    @Test
    void createUser_WhenEmailTaken_ShouldThrowException() {
        // Arrange - Setup mock for existing email
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // Act & Assert - Verify exception
        assertThrows(UserAlreadyExistsException.class, () -> {
            userService.createUser(testUserDto);
        });

        // Verify - Check repository interactions
        verify(userRepository, times(1)).existsByEmail(testUserDto.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    /**
     * Tests successful user update scenario
     * Verifies:
     * - User existence check
     * - Email availability check
     * - Update operation
     * - Response mapping
     */
    @Test
    void updateUser_WhenUserExistsAndEmailNotTaken_ShouldUpdateUser() {
        // Arrange - Setup test data and mocks
        UserDto updateDto = new UserDto("updated@example.com", "Updated Name");
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail(updateDto.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act - Perform update
        UserResponse response = userService.updateUser(1L, updateDto);

        // Assert and Verify
        assertNotNull(response);
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).existsByEmail(updateDto.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    /**
     * Tests retrieval of all users
     * Verifies:
     * - List retrieval
     * - Response mapping
     * - Repository interaction
     */
    @Test
    void getAllUsers_ShouldReturnListOfUsers() {
        // Arrange - Setup mock data
        List<User> users = Arrays.asList(testUser);
        when(userRepository.findAll()).thenReturn(users);

        // Act - Get all users
        List<UserResponse> responses = userService.getAllUsers();

        // Assert and Verify
        assertNotNull(responses);
        assertEquals(1, responses.size());
        verify(userRepository, times(1)).findAll();
    }

    /**
     * Tests successful user deletion
     * Verifies:
     * - Existence check
     * - Deletion operation
     * - Repository interactions
     */
    @Test
    void deleteUser_WhenUserExists_ShouldDeleteUser() {
        // Arrange - Setup mock for existing user
        when(userRepository.existsById(anyLong())).thenReturn(true);
        doNothing().when(userRepository).deleteById(anyLong());

        // Act - Delete user
        userService.deleteUser(1L);

        // Verify - Check correct method calls
        verify(userRepository, times(1)).existsById(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }

    /**
     * Tests deletion of non-existent user
     * Verifies:
     * - Exception throwing
     * - No deletion attempt
     * - Repository interactions
     */
    @Test
    void deleteUser_WhenUserDoesNotExist_ShouldThrowException() {
        // Arrange - Setup mock for non-existent user
        when(userRepository.existsById(anyLong())).thenReturn(false);

        // Act & Assert - Verify exception
        assertThrows(UserNotFoundException.class, () -> {
            userService.deleteUser(1L);
        });

        // Verify - Check repository interactions
        verify(userRepository, times(1)).existsById(1L);
        verify(userRepository, never()).deleteById(anyLong());
    }

    /**
     * Tests successful user retrieval by ID
     * Verifies:
     * - User finding
     * - Response mapping
     * - Repository interaction
     */
    @Test
    void getUserById_WhenUserExists_ShouldReturnUser() {
        // Arrange - Setup mock for existing user
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));

        // Act - Get user
        UserResponse response = userService.getUserById(1L);

        // Assert and Verify
        assertNotNull(response);
        assertEquals(testUser.getId(), response.getId());
        verify(userRepository, times(1)).findById(1L);
    }

    /**
     * Tests retrieval of non-existent user
     * Verifies:
     * - Exception throwing
     * - Repository interaction
     */
    @Test
    void getUserById_WhenUserDoesNotExist_ShouldThrowException() {
        // Arrange - Setup mock for non-existent user
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert - Verify exception
        assertThrows(UserNotFoundException.class, () -> {
            userService.getUserById(1L);
        });

        // Verify - Check repository interaction
        verify(userRepository, times(1)).findById(1L);
    }
}