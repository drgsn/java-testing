package com.itadviser;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class UserActivityTracker {
    private final Clock clock;

    public UserActivityTracker(Clock clock) {
        this.clock = clock;
    }

    public UserActivity createActivity(User user) {
        return new UserActivity(user.getId(), LocalDateTime.now(clock));
    }

    public Duration getInactiveDuration(UserActivity activity) {
        LocalDateTime now = LocalDateTime.now(clock);
        return Duration.between(activity.getLastLoginTime(), now);
    }

    public void recordLogin(UserActivity activity) {
        activity.setLastLoginTime(LocalDateTime.now(clock));
    }
}