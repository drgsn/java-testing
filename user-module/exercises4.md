## Implement time based tests for spring scheduled

given this class 

```java
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class UserActivityCleanupService {
    private final UserRepository userRepository;
    private final UserActivityTracker activityTracker;
    private final Clock clock;

    // Configurable threshold for inactive users (default: 90 days)
    private final Duration inactivityThreshold;

    public UserActivityCleanupService(
        UserRepository userRepository,
        UserActivityTracker activityTracker,
        Clock clock,
        Duration inactivityThreshold
    ) {
        this.userRepository = userRepository;
        this.activityTracker = activityTracker;
        this.clock = clock;
        this.inactivityThreshold = inactivityThreshold;
    }

    @Scheduled(cron = "0 0 0 * * *") // Run at midnight every day
    @Transactional
    public void cleanupInactiveUsers() {
        log.info("Starting inactive users cleanup");
        LocalDateTime now = LocalDateTime.now(clock);
        LocalDateTime thresholdDate = now.minus(inactivityThreshold);

        List<User> inactiveUsers = userRepository.findByUpdatedAtBefore(thresholdDate);
        if (!inactiveUsers.isEmpty()) {
            log.info("Found {} inactive users to clean up", inactiveUsers.size());
            userRepository.deleteAll(inactiveUsers);
        }
        log.info("Completed inactive users cleanup");
    }

    @Scheduled(fixedRate = 3600000) // Run every hour
    @Transactional
    public void updateUserActivities() {
        log.info("Starting user activity update");
        List<User> users = userRepository.findAll();
        int updatedCount = 0;

        for (User user : users) {
            UserActivity activity = activityTracker.createActivity(user);
            Duration inactiveDuration = activityTracker.getInactiveDuration(activity);

            if (inactiveDuration.compareTo(Duration.ofDays(30)) > 0) {
                log.warn("User {} inactive for {} days", user.getEmail(),
                    inactiveDuration.toDays());
                user.setUpdatedAt(LocalDateTime.now(clock));
                userRepository.save(user);
                updatedCount++;
            }
        }

        log.info("Completed user activity update. Updated {} users", updatedCount);
    }
}
```


and update user repository

```java
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findByUpdatedAtBefore(LocalDateTime date);
}
```

with this config 

```java
import java.time.Clock;
import java.time.Duration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class SchedulingConfig {
    
    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
    
    @Bean
    public Duration inactivityThreshold() {
        return Duration.ofDays(90);
    }
}

```

write a test that demonstrates the following

1. Clock Injection: Using a Clock instance for time-based operations makes testing deterministic.
2. Mocking Dependencies: Using Mockito to mock the repository and activity tracker.
3. Verifying Scheduled Annotations: Testing that the scheduling configuration is correct using ScheduledAnnotationBeanPostProcessor.
4. Testing Business Logic: Testing the actual business logic independently of the scheduling.
5. Nested Test Classes: Organizing tests by functionality using @Nested classes.
6. Argument Captors: Using ArgumentCaptor to verify the exact data being passed to repository methods.


Point of start

```java

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.test.context.event.annotation.BeforeTestClass;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserActivityCleanupService Tests")
class UserActivityCleanupServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserActivityTracker activityTracker;

    @Captor
    private ArgumentCaptor<List<User>> usersCaptor;

    private Clock fixedClock;
    private UserActivityCleanupService cleanupService;
    private static final ZoneId TEST_ZONE = ZoneId.systemDefault();

    @BeforeEach
    void setUp() {
        // Set up a fixed clock at 2024-03-14 10:00:00
        Instant fixedInstant = Instant.parse("2024-03-14T10:00:00Z");
        fixedClock = Clock.fixed(fixedInstant, TEST_ZONE);
        
        // Initialize service with 90-day inactivity threshold
        cleanupService = new UserActivityCleanupService(
            userRepository,
            activityTracker,
            fixedClock,
            Duration.ofDays(90)
        );
    }

    @Nested
    @DisplayName("Cleanup Inactive Users Tests")
    class CleanupInactiveUsersTests {
        
        @Test
        @DisplayName("Should delete users inactive for more than 90 days")
        void shouldDeleteInactiveUsers() {
            
        }

        @Test
        @DisplayName("Should not delete any users when all are active")
        void shouldNotDeleteActiveUsers() {
        }
    }

    @Nested
    @DisplayName("Update User Activities Tests")
    class UpdateUserActivitiesTests {

        @Test
        @DisplayName("Should update users inactive for more than 30 days")
        void shouldUpdateInactiveUsers() {
            
        }
    }

    @Test
    @DisplayName("Verify scheduled tasks are registered")
    void verifyScheduledTasks() {
        
    }

    private User createUserWithUpdatedAt(String email, LocalDateTime updatedAt) {
        User user = new User(email, "Test User");
        user.setId((long) email.hashCode());
        user.setUpdatedAt(updatedAt);
        return user;
    }
}

```