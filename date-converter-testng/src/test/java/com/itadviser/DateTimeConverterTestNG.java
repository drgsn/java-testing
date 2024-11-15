package com.itadviser;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertThrows;

import com.itadviser.DateTimeConverter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * A test class that demonstrates key TestNG testing features and best practices for testing date/time conversion
 * functionality.
 *
 * <p>This test suite showcases various important TestNG features including:</p>
 *
 * <h3>Annotations:</h3>
 * <ul>
 *   <li>{@code @Test} - Marks methods as test cases with additional attributes like timeOut</li>
 *   <li>{@code @BeforeSuite} - Executes setup code once before all tests in the suite</li>
 *   <li>{@code @BeforeTest} - Executes before each test tag in testng.xml</li>
 *   <li>{@code @BeforeClass} - Executes before the first test method in the current class</li>
 *   <li>{@code @BeforeMethod} - Executes before each test method</li>
 *   <li>{@code @AfterMethod} - Executes after each test method</li>
 *   <li>{@code @AfterClass} - Executes after all test methods in the current class</li>
 *   <li>{@code @AfterTest} - Executes after each test tag in testng.xml</li>
 *   <li>{@code @AfterSuite} - Executes after all tests in the suite</li>
 *   <li>{@code @DataProvider} - Provides data for test methods</li>
 * </ul>
 *
 * <h3>Assertion Examples:</h3>
 * <ul>
 *   <li>{@code assertEquals} - Validates expected equals actual</li>
 *   <li>{@code assertThrows} - Verifies exception throwing behavior</li>
 *   <li>{@code assertNotNull} - Verifies object is not null</li>
 *   <li>Custom failure messages for clear test feedback</li>
 * </ul>
 *
 * <h3>Known Test Failures:</h3>
 * <p>Due to an introduced bug, the following tests are expected to fail:</p>
 * <ul>
 *   <li>{@code testJodaToJavaConversion} - Fails due to incorrect nanosecond conversion</li>
 *   <li>{@code testRoundTripConversion} - Fails due to data loss during conversion</li>
 *   <li>{@code testWithMultipleMillisecondValues} - Fails for all test cases</li>
 * </ul>
 *
 * @see DateTimeConverter
 * @since 1.0
 */
public class DateTimeConverterTestNG {

    private static org.joda.time.LocalDateTime jodaDateTime;
    private static java.time.LocalDateTime javaDateTime;

    @BeforeSuite
    public void beforeSuite() {
        System.out.println("Starting DateTimeConverter TestNG test suite");
    }

    @BeforeMethod
    public void setUp() {
        jodaDateTime = new org.joda.time.LocalDateTime(2024, 3, 14, 15, 30, 45, 500);
        javaDateTime = java.time.LocalDateTime.of(2024, 3, 14, 15, 30, 45, 500_000_000);
    }

    @Test(description = "Converting from Java to Joda maintains precision")
    public void testJavaToJodaConversion() {
        org.joda.time.LocalDateTime converted = DateTimeConverter.toJodaDateTime(javaDateTime);

        // TestNG doesn't have assertAll, so we test each component separately
        assertEquals(converted.getYear(), javaDateTime.getYear(), "Year should match");
        assertEquals(converted.getMonthOfYear(), javaDateTime.getMonthValue(), "Month should match");
        assertEquals(converted.getDayOfMonth(), javaDateTime.getDayOfMonth(), "Day should match");
        assertEquals(converted.getHourOfDay(), javaDateTime.getHour(), "Hour should match");
        assertEquals(converted.getMinuteOfHour(), javaDateTime.getMinute(), "Minute should match");
        assertEquals(converted.getSecondOfMinute(), javaDateTime.getSecond(), "Second should match");
        assertEquals(converted.getMillisOfSecond(), javaDateTime.getNano() / 1_000_000, "Millis should match");
    }

    @Test(description = "Converting from Joda to Java reveals precision bug")
    public void testJodaToJavaConversion() {
        java.time.LocalDateTime converted = DateTimeConverter.toJavaDateTime(jodaDateTime);

        // This test will fail due to the bug!
        assertEquals(converted.getNano(), 500_000_000,
            "Nanoseconds should be 500,000,000 but was " + converted.getNano());
    }

    @Test(description = "Round trip conversion shows data loss")
    public void testRoundTripConversion() {
        // Convert Joda -> Java -> Joda
        java.time.LocalDateTime intermediateJava = DateTimeConverter.toJavaDateTime(jodaDateTime);
        org.joda.time.LocalDateTime roundTripped = DateTimeConverter.toJodaDateTime(intermediateJava);

        // This assertion will fail due to the bug
        assertEquals(roundTripped.getMillisOfSecond(), jodaDateTime.getMillisOfSecond(),
            "Milliseconds should be preserved in round trip conversion");
    }

    @DataProvider(name = "millisecondValues")
    public Object[][] millisecondValues() {
        return new Object[][]{
            {100}, {200}, {300}, {400}, {500}
        };
    }

    @Test(dataProvider = "millisecondValues", description = "Test multiple millisecond values")
    public void testWithMultipleMillisecondValues(int milliseconds) {
        org.joda.time.LocalDateTime original = jodaDateTime.withMillisOfSecond(milliseconds);
        java.time.LocalDateTime converted = DateTimeConverter.toJavaDateTime(original);

        // This will fail due to the bug
        assertEquals(converted.getNano(), milliseconds * 1_000_000,
            "Nanoseconds conversion failed for " + milliseconds + " milliseconds");
    }

    @Test(description = "Null input handling")
    public void testNullHandling() {
        assertThrows(NullPointerException.class, () -> DateTimeConverter.toJavaDateTime(null));
    }

    @Test(timeOut = 1000, description = "Performance test")
    public void testConversionPerformance() {
        // Test should complete within 1 second
        for (int i = 0; i < 1000; i++) {
            DateTimeConverter.toJavaDateTime(jodaDateTime);
            DateTimeConverter.toJodaDateTime(javaDateTime);
        }
    }

    @AfterMethod
    public void tearDown() {
        jodaDateTime = null;
        javaDateTime = null;
    }

    @AfterSuite
    public void afterSuite() {
        System.out.println("Completed all DateTimeConverter TestNG tests");
    }
}