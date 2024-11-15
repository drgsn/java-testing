package com.itadviser;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserActivity {
    private final Long userId;
    private LocalDateTime lastLoginTime;
    private LocalDateTime createdTime;

    public UserActivity(Long userId, LocalDateTime createdTime) {
        this.userId = userId;
        this.createdTime = createdTime;
        this.lastLoginTime = createdTime;
    }
}