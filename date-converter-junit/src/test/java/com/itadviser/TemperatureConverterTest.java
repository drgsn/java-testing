package com.itadviser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.itadvisor.TemperatureConverter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Test class for TemperatureConverter.
 * <p>
 * Exercises:
 * 1. Implement the missing test methods
 *
 * 2. Find and document the bug in the converter
 *
 * 3. Add more test cases using @ParameterizedTest
 *
 * 4. Add tests for invalid temperatures (below absolute zero)
 *
 * 5. Calculate and verify round-trip conversions
 *
 *
 * Assignment Tasks:
 * 1. Complete the TODO sections in TemperatureConverterTest
 *
 * 2. Find if there is any bugs in TemperatureConverter
 *
 * 3. Add at least 3 parameterized test cases
 *
 * 4. Implement round-trip conversion tests
 *
 * 5. Add validation for physically impossible temperatures
 *
 * 6. Document your test cases and findings
 *
 * 7. Suggest improvements to the code
 *
 * Bonus Challenges:
 * 1. Add temperature format conversion (e.g., "72°F" -> 72.0)
 * 2. Add precision control for conversions
 * 3. Implement temperature range checks
 * 4. Create a custom assertion for temperature comparisons
 * </p>
 */
@DisplayName("Temperature Converter Tests")
class TemperatureConverterTest {

    // TODO: Implement these test methods
    @Test
    @DisplayName("Water freezing point conversion")
    void testWaterFreezingPoint() {
        // Test conversion of 0°C (water freezing point)
        // Should be 32°F and 273.15K
    }

    @Test
    @DisplayName("Water boiling point conversion")
    void testWaterBoilingPoint() {
        // Test conversion of 100°C (water boiling point)
        // Should be 212°F and 373.15K
    }

    @Test
    @DisplayName("Room temperature conversion")
    void testRoomTemperature() {
        // Test conversion of 20°C (room temperature)
        // Should be 68°F and 293.15K
    }

    @Test
    @DisplayName("Absolute zero conversion")
    void testAbsoluteZero() {

    }

    // TODO: Add parameterized tests for multiple temperature values
    @ParameterizedTest
    @CsvSource({
        // Add test data in format: celsius,fahrenheit,kelvin
        // Example: "0,32,273.15"
    })
    @DisplayName("Temperature conversions parameterized test")
    void testTemperatureConversions(double celsius, double fahrenheit, double kelvin) {
        // Implement conversion tests using parameters
    }

    // TODO: Add round-trip conversion tests
    @Test
    @DisplayName("Round-trip conversion test")
    void testRoundTripConversion() {
        // Test converting from one unit to another and back
        // Should get the original value (within rounding errors)
    }

    // TODO: Add invalid temperature tests
    @Test
    @DisplayName("Invalid temperature test")
    void testInvalidTemperature() {
        // Test temperatures below absolute zero
    }
}