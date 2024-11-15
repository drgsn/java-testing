package com.itadviser.context;

import static org.junit.Assert.assertNotNull;

import com.itadviser.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
class MainTest {

    @Autowired
    private UserService userService;

    @Test
    void contextLoads() {
        assertNotNull(userService);
    }
}
