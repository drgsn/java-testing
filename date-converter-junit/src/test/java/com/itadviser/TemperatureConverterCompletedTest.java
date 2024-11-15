package com.itadviser;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.itadvisor.TemperatureConverter;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Comprehensive test suite for TemperatureConverter class.
 * This test class demonstrates:
 * 1. Basic unit testing of temperature conversions
 * 2. Parameterized testing for multiple values
 * 3. Round-trip conversion verification
 * 4. Edge case testing
 * 5. Invalid input handling
 */
@DisplayName("Temperature Converter Tests")
class TemperatureConverterCompletedTest {

    private static final double DELTA = 0.01; // Allowed difference for double comparisons

    @BeforeAll
    static void setUp() {
        System.out.println("Starting Temperature Converter tests");
    }

    @Test
    @DisplayName("Water freezing point conversion")
    void testWaterFreezingPoint() {
        // Test conversion of 0°C (water freezing point)
        double celsiusTemp = 0.0;

        assertEquals(32.0,
            TemperatureConverter.celsiusToFahrenheit(celsiusTemp),
            DELTA,
            "Incorrect freezing point in Fahrenheit");

        assertEquals(273.15,
            TemperatureConverter.celsiusToKelvin(celsiusTemp),
            DELTA,
            "Incorrect freezing point in Kelvin");
    }

    @Test
    @DisplayName("Water boiling point conversion")
    void testWaterBoilingPoint() {
        // Test conversion of 100°C (water boiling point)
        double celsiusTemp = 100.0;

        assertEquals(212.0,
            TemperatureConverter.celsiusToFahrenheit(celsiusTemp),
            DELTA,
            "Incorrect boiling point in Fahrenheit");

        assertEquals(373.15,
            TemperatureConverter.celsiusToKelvin(celsiusTemp),
            DELTA,
            "Incorrect boiling point in Kelvin");
    }

    @Test
    @DisplayName("Room temperature conversion")
    void testRoomTemperature() {
        // Test conversion of 20°C (room temperature)
        double celsiusTemp = 20.0;

        assertEquals(68.0,
            TemperatureConverter.celsiusToFahrenheit(celsiusTemp),
            DELTA,
            "Incorrect room temperature in Fahrenheit");

        assertEquals(293.15,
            TemperatureConverter.celsiusToKelvin(celsiusTemp),
            DELTA,
            "Incorrect room temperature in Kelvin");
    }

    @Test
    @DisplayName("Absolute zero conversion")
    void testAbsoluteZero() {
        double absoluteZeroC = -273.15;
        double absoluteZeroF = -459.67;
        double absoluteZeroK = 0.0;

        assertEquals(absoluteZeroK,
            TemperatureConverter.celsiusToKelvin(absoluteZeroC),
            DELTA,
            "Celsius to Kelvin conversion at absolute zero failed");

        assertEquals(absoluteZeroF,
            TemperatureConverter.celsiusToFahrenheit(absoluteZeroC),
            DELTA,
            "Celsius to Fahrenheit conversion at absolute zero failed");
    }

    @ParameterizedTest
    @CsvSource({
        "0,32.0,273.15",      // Freezing point
        "100,212.0,373.15",   // Boiling point
        "20,68.0,293.15",     // Room temperature
        "-40,-40.0,233.15",   // Equal point for C and F
        "-273.15,-459.67,0",  // Absolute zero
        "37,98.6,310.15"      // Body temperature
    })
    @DisplayName("Temperature conversions parameterized test")
    void testTemperatureConversions(double celsius, double fahrenheit, double kelvin) {
        assertAll("Temperature conversions",
            () -> assertEquals(fahrenheit,
                TemperatureConverter.celsiusToFahrenheit(celsius),
                DELTA,
                "Celsius to Fahrenheit conversion failed"),
            () -> assertEquals(kelvin,
                TemperatureConverter.celsiusToKelvin(celsius),
                DELTA,
                "Celsius to Kelvin conversion failed"),
            () -> assertEquals(celsius,
                TemperatureConverter.fahrenheitToCelsius(fahrenheit),
                DELTA,
                "Fahrenheit to Celsius conversion failed"),
            () -> assertEquals(celsius,
                TemperatureConverter.kelvinToCelsius(kelvin),
                DELTA,
                "Kelvin to Celsius conversion failed")
        );
    }

    @Test
    @DisplayName("Round-trip conversion test")
    void testRoundTripConversion() {
        double originalCelsius = 25.0;

        // Test Celsius -> Fahrenheit -> Celsius
        double roundTripC = TemperatureConverter.fahrenheitToCelsius(
            TemperatureConverter.celsiusToFahrenheit(originalCelsius)
        );
        assertEquals(originalCelsius, roundTripC, DELTA,
            "Round trip through Fahrenheit failed");

        // Test Celsius -> Kelvin -> Celsius
        roundTripC = TemperatureConverter.kelvinToCelsius(
            TemperatureConverter.celsiusToKelvin(originalCelsius)
        );
        assertEquals(originalCelsius, roundTripC, DELTA,
            "Round trip through Kelvin failed");
    }

    @ParameterizedTest
    @ValueSource(doubles = {-274.0, -300.0, -500.0})
    @DisplayName("Invalid temperature test")
    void testInvalidTemperature(double kelvin) {
        assertFalse(TemperatureConverter.isValidTemperature(kelvin),
            "Temperature below absolute zero should be invalid: " + kelvin);
    }

    @Test
    @DisplayName("Bug demonstration test")
    void testBugInCelsiusToKelvin() {
        double testCelsius = 0.0; // freezing point
        double expectedKelvin = 273.15;
        double actualKelvin = TemperatureConverter.celsiusToKelvin(testCelsius);

        // This test will fail due to the bug
        assertEquals(expectedKelvin, actualKelvin, DELTA,
            String.format("Bug detected: Expected %.2fK but got %.2fK. " +
                          "The celsiusToKelvin method is subtracting the Kelvin constant " +
                          "instead of adding it.", expectedKelvin, actualKelvin));
    }

    @Test
    @DisplayName("Method chain test")
    void testMethodChaining() {
        double originalCelsius = 50.0;

        // Convert Celsius -> Kelvin -> Fahrenheit -> Celsius
        double result = TemperatureConverter.fahrenheitToCelsius(
            TemperatureConverter.kelvinToFahrenheit(
                TemperatureConverter.celsiusToKelvin(originalCelsius)
            )
        );

        assertEquals(originalCelsius, result, DELTA,
            "Method chaining conversion failed");
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTest {

        @Test
        @DisplayName("Test very high temperatures")
        void testHighTemperatures() {
            double veryCelsius = 1_000_000.0;
            assertTrue(TemperatureConverter.celsiusToFahrenheit(veryCelsius) > 0,
                "Very high temperature conversion failed");
        }

        @Test
        @DisplayName("Test temperatures near absolute zero")
        void testNearAbsoluteZero() {
            double nearlyCelsius = -273.14; // Just above absolute zero
            assertTrue(TemperatureConverter.isValidTemperature(
                    TemperatureConverter.celsiusToKelvin(nearlyCelsius)),
                "Temperature near absolute zero should be valid");
        }
    }

    @AfterAll
    static void tearDown() {
        System.out.println("Completed Temperature Converter tests");
    }
}