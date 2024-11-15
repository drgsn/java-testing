package com.itadviser;


import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse createUser(UserDto userDto) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new UserAlreadyExistsException("User with email " + userDto.getEmail() + " already exists");
        }

        User user = new User(userDto.getEmail(), userDto.getName());
        User savedUser = userRepository.save(user);
        return convertToResponse(savedUser);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        return convertToResponse(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
            .map(this::convertToResponse)
            .toList();
    }

    public UserResponse updateUser(Long id, UserDto userDto) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        // Check if new email is already taken by another user
        if (!user.getEmail().equals(userDto.getEmail()) &&
            userRepository.existsByEmail(userDto.getEmail())) {
            throw new UserAlreadyExistsException("Email " + userDto.getEmail() + " is already taken");
        }

        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setUpdatedAt(LocalDateTime.now());

        User updatedUser = userRepository.save(user);
        return convertToResponse(updatedUser);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    private UserResponse convertToResponse(User user) {
        return new UserResponse(
            user.getId(),
            user.getEmail(),
            user.getName(),
            user.getCreatedAt(),
            user.getUpdatedAt()
        );
    }
}
