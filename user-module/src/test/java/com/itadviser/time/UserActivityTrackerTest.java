package com.itadviser.time;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.itadviser.User;
import com.itadviser.UserActivity;
import com.itadviser.UserActivityTracker;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * This implementation demonstrates several important concepts for testing time-dependent code:
 *
 * Clock Injection: Instead of relying on system time, we inject a Clock instance, making the code testable.
 * Fixed Clock: We use Clock.fixed() to create a clock that always returns the same time, making our tests deterministic.
 * Time Manipulation: The tests demonstrate how to "move time forward" by creating new clock instances with different fixed times.
 * Duration Testing: We test both absolute times and durations between events.
 * Multiple Scenarios: The tests cover various scenarios including:
 *
 * Initial activity creation
 * Measuring inactivity periods
 * Recording new logins
 * Verifying that creation time remains unchanged while login time updates
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserActivityTracker Time-Based Tests")
class UserActivityTrackerTest {

    private static final ZoneId TEST_ZONE = ZoneId.systemDefault();
    private Clock fixedClock;
    private UserActivityTracker activityTracker;

    @BeforeEach
    void setUp() {
        // Start with a fixed time: 2024-03-14 10:00:00
        Instant initialTime = Instant.parse("2024-03-14T10:00:00Z");
        fixedClock = Clock.fixed(initialTime, TEST_ZONE);
        activityTracker = new UserActivityTracker(fixedClock);
    }

    @Test
    @DisplayName("Should track user inactivity duration accurately")
    void shouldTrackInactivityDuration() {
        // Given
        User user = new User("test@example.com", "Test User");
        user.setId(1L);
        UserActivity activity = activityTracker.createActivity(user);

        // Initial login time should be 10:00
        assertEquals(
            LocalDateTime.now(fixedClock),
            activity.getLastLoginTime(),
            "Initial login time should match creation time"
        );

        // When - Advance clock by 30 minutes
        fixedClock = Clock.fixed(
            Instant.parse("2024-03-14T10:30:00Z"),
            TEST_ZONE
        );
        activityTracker = new UserActivityTracker(fixedClock);

        // Then - Check inactivity duration
        Duration inactiveDuration = activityTracker.getInactiveDuration(activity);
        assertEquals(
            Duration.ofMinutes(30),
            inactiveDuration,
            "Should show 30 minutes of inactivity"
        );

        // When - User logs in again
        activityTracker.recordLogin(activity);

        // Then - Inactivity duration should reset
        inactiveDuration = activityTracker.getInactiveDuration(activity);
        assertEquals(
            Duration.ZERO,
            inactiveDuration,
            "Inactivity duration should reset after login"
        );

        // When - Advance clock by 1 hour
        fixedClock = Clock.fixed(
            Instant.parse("2024-03-14T11:30:00Z"),
            TEST_ZONE
        );
        activityTracker = new UserActivityTracker(fixedClock);

        // Then - Check new inactivity duration
        inactiveDuration = activityTracker.getInactiveDuration(activity);
        assertEquals(
            Duration.ofHours(1),
            inactiveDuration,
            "Should show 1 hour of inactivity"
        );
    }

    @Test
    @DisplayName("Should preserve creation time while updating login time")
    void shouldPreserveCreationTime() {
        // Given
        User user = new User("test@example.com", "Test User");
        user.setId(1L);
        UserActivity activity = activityTracker.createActivity(user);
        LocalDateTime creationTime = activity.getCreatedTime();

        // When - Advance clock and record multiple logins
        fixedClock = Clock.fixed(
            Instant.parse("2024-03-14T11:00:00Z"),
            TEST_ZONE
        );
        activityTracker = new UserActivityTracker(fixedClock);
        activityTracker.recordLogin(activity);

        fixedClock = Clock.fixed(
            Instant.parse("2024-03-14T12:00:00Z"),
            TEST_ZONE
        );
        activityTracker = new UserActivityTracker(fixedClock);
        activityTracker.recordLogin(activity);

        // Then
        assertThat(activity.getCreatedTime())
            .isEqualTo(creationTime)
            .isBefore(activity.getLastLoginTime());
    }
}