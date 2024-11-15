package com.itadviser;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;


/**
 * A test class that demonstrates key JUnit 5 testing features and best practices for
 * testing date/time conversion functionality.
 *
 * <p>This test suite showcases various important JUnit 5 features including:</p>
 *
 * <h3>Annotations:</h3>
 * <ul>
 *   <li>{@code @DisplayName} - Provides human-readable test names</li>
 *   <li>{@code @BeforeAll} - Executes setup code once before all tests</li>
 *   <li>{@code @BeforeEach} - Executes setup code before each individual test</li>
 *   <li>{@code @AfterEach} - Executes cleanup code after each individual test</li>
 *   <li>{@code @AfterAll} - Executes cleanup code once after all tests</li>
 *   <li>{@code @Test} - Marks methods as test cases</li>
 *   <li>{@code @ParameterizedTest} - Enables test execution with different parameters</li>
 *   <li>{@code @Timeout} - Enforces time limits for test execution</li>
 * </ul>
 *
 * <h3>Assertion Examples:</h3>
 * <ul>
 *   <li>{@code assertEquals} - Validates expected equals actual</li>
 *   <li>{@code assertThrows} - Verifies exception throwing behavior</li>
 *   <li>{@code assertAll} - Groups multiple assertions together</li>
 *   <li>Custom failure messages for clear test feedback</li>
 * </ul>
 *
 * <h3>Test Organization Features:</h3>
 * <ul>
 *   <li>Structured setup and teardown methods</li>
 *   <li>Parameterized tests using {@code @ValueSource}</li>
 *   <li>Clear and descriptive test naming conventions</li>
 *   <li>Independent and isolated test cases</li>
 * </ul>
 *
 * <h3>Known Test Failures:</h3>
 * <p>Due to an introduced bug, the following tests are expected to fail:</p>
 * <ul>
 *   <li>{@code testJodaToJavaConversion} - Fails due to incorrect nanosecond conversion</li>
 *   <li>{@code testRoundTripConversion} - Fails due to data loss during conversion</li>
 *   <li>{@code testMultipleMillisecondValues} - Fails for all test cases</li>
 * </ul>
 *
 * <h3>Passing Tests Demonstrate:</h3>
 * <ul>
 *   <li>Proper handling of null values</li>
 *   <li>Compliance with performance requirements</li>
 *   <li>Correct conversion of basic date/time fields (excluding millisecond components)</li>
 * </ul>
 *
 * @see DateTimeConverter
 * @since 1.0
 */
@DisplayName("DateTimeConverter Tests")
class DateTimeConverterTest {

    private static org.joda.time.LocalDateTime jodaDateTime;
    private static java.time.LocalDateTime javaDateTime;

    @BeforeAll
    static void setUp() {
        // This method runs once before all test methods
        System.out.println("Starting DateTimeConverter tests");
    }

    @BeforeEach
    void init() {
        // This method runs before each test
        jodaDateTime = new org.joda.time.LocalDateTime(2024, 3, 14, 15, 30, 45, 500);
        javaDateTime = java.time.LocalDateTime.of(2024, 3, 14, 15, 30, 45, 500_000_000);
    }

    @Test
    @DisplayName("Converting from Java to Joda maintains precision")
    void testJavaToJodaConversion() {
        org.joda.time.LocalDateTime converted = com.itadviser.DateTimeConverter.toJodaDateTime(javaDateTime);

        assertAll("DateTime components should match",
            () -> assertEquals(javaDateTime.getYear(), converted.getYear()),
            () -> assertEquals(javaDateTime.getMonthValue(), converted.getMonthOfYear()),
            () -> assertEquals(javaDateTime.getDayOfMonth(), converted.getDayOfMonth()),
            () -> assertEquals(javaDateTime.getHour(), converted.getHourOfDay()),
            () -> assertEquals(javaDateTime.getMinute(), converted.getMinuteOfHour()),
            () -> assertEquals(javaDateTime.getSecond(), converted.getSecondOfMinute()),
            () -> assertEquals(javaDateTime.getNano() / 1_000_000, converted.getMillisOfSecond())
        );
    }

    @Test
    @DisplayName("Converting from Joda to Java reveals precision bug")
    void testJodaToJavaConversion() {
        java.time.LocalDateTime converted = com.itadviser.DateTimeConverter.toJavaDateTime(jodaDateTime);

        // This test will fail due to the bug!
        assertEquals(500_000_000, converted.getNano(),
            "Nanoseconds should be 500,000,000 but was " + converted.getNano());
    }

    @Test
    @DisplayName("Round trip conversion shows data loss")
    void testRoundTripConversion() {
        // Convert Joda -> Java -> Joda
        java.time.LocalDateTime intermediateJava = com.itadviser.DateTimeConverter.toJavaDateTime(jodaDateTime);
        org.joda.time.LocalDateTime roundTripped = com.itadviser.DateTimeConverter.toJodaDateTime(intermediateJava);

        // This assertion will fail due to the bug
        assertEquals(jodaDateTime.getMillisOfSecond(), roundTripped.getMillisOfSecond(),
            "Milliseconds should be preserved in round trip conversion");
    }

    @ParameterizedTest
    @ValueSource(ints = {100, 200, 300, 400, 500})
    @DisplayName("Test multiple millisecond values")
    void testMultipleMillisecondValues(int milliseconds) {
        org.joda.time.LocalDateTime original = jodaDateTime.withMillisOfSecond(milliseconds);
        java.time.LocalDateTime converted = com.itadviser.DateTimeConverter.toJavaDateTime(original);

        // This will fail due to the bug
        assertEquals(milliseconds * 1_000_000, converted.getNano(),
            "Nanoseconds conversion failed for " + milliseconds + " milliseconds");
    }

    @Test
    @DisplayName("Null input handling")
    void testNullHandling() {
        assertThrows(NullPointerException.class, () -> {
            com.itadviser.DateTimeConverter.toJavaDateTime(null);
        }, "Should throw NullPointerException for null input");
    }

    @Test
    @Timeout(1)
    @DisplayName("Performance test")
    void testConversionPerformance() {
        // Test should complete within 1 second
        for (int i = 0; i < 1000; i++) {
            com.itadviser.DateTimeConverter.toJavaDateTime(jodaDateTime);
            com.itadviser.DateTimeConverter.toJodaDateTime(javaDateTime);
        }
    }

    @AfterEach
    void tearDown() {
        // Cleanup after each test
        jodaDateTime = null;
        javaDateTime = null;
    }

    @AfterAll
    static void done() {
        System.out.println("Completed all DateTimeConverter tests");
    }
}