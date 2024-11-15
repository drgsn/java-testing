package com.itadviser;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import com.itadvisor.TemperatureConverter;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Comprehensive test suite for TemperatureConverter class using TestNG.
 * This test class demonstrates:
 * 1. Basic unit testing of temperature conversions
 * 2. Data-driven testing using @DataProvider
 * 3. Round-trip conversion verification
 * 4. Edge case testing
 * 5. Invalid input handling
 * 6. Test grouping and dependencies
 */
public class TemperatureConverterTestNG {

    private static final double DELTA = 0.01; // Allowed difference for double comparisons

    @BeforeSuite
    public void setUp() {
        System.out.println("Starting Temperature Converter TestNG tests");
    }

    @Test(groups = "basicConversions")
    public void testWaterFreezingPoint() {
        // Test conversion of 0°C (water freezing point)
        double celsiusTemp = 0.0;

        assertEquals(
            TemperatureConverter.celsiusToFahrenheit(celsiusTemp),
            32.0,
            DELTA,
            "Incorrect freezing point in Fahrenheit"
        );

        assertEquals(
            TemperatureConverter.celsiusToKelvin(celsiusTemp),
            273.15,
            DELTA,
            "Incorrect freezing point in Kelvin"
        );
    }

    @Test(groups = "basicConversions")
    public void testWaterBoilingPoint() {
        // Test conversion of 100°C (water boiling point)
        double celsiusTemp = 100.0;

        assertEquals(
            TemperatureConverter.celsiusToFahrenheit(celsiusTemp),
            212.0,
            DELTA,
            "Incorrect boiling point in Fahrenheit"
        );

        assertEquals(
            TemperatureConverter.celsiusToKelvin(celsiusTemp),
            373.15,
            DELTA,
            "Incorrect boiling point in Kelvin"
        );
    }

    @DataProvider(name = "temperatureConversions")
    public Object[][] getTemperatureData() {
        return new Object[][] {
            {0.0, 32.0, 273.15},      // Freezing point
            {100.0, 212.0, 373.15},   // Boiling point
            {20.0, 68.0, 293.15},     // Room temperature
            {-40.0, -40.0, 233.15},   // Equal point for C and F
            {-273.15, -459.67, 0.0},  // Absolute zero
            {37.0, 98.6, 310.15}      // Body temperature
        };
    }

    @Test(dataProvider = "temperatureConversions", groups = "parameterized")
    public void testTemperatureConversions(double celsius, double fahrenheit, double kelvin) {
        assertEquals(
            TemperatureConverter.celsiusToFahrenheit(celsius),
            fahrenheit,
            DELTA,
            "Celsius to Fahrenheit conversion failed"
        );

        assertEquals(
            TemperatureConverter.celsiusToKelvin(celsius),
            kelvin,
            DELTA,
            "Celsius to Kelvin conversion failed"
        );

        assertEquals(
            TemperatureConverter.fahrenheitToCelsius(fahrenheit),
            celsius,
            DELTA,
            "Fahrenheit to Celsius conversion failed"
        );

        assertEquals(
            TemperatureConverter.kelvinToCelsius(kelvin),
            celsius,
            DELTA,
            "Kelvin to Celsius conversion failed"
        );
    }

    @Test(groups = "roundTrip")
    public void testRoundTripConversion() {
        double originalCelsius = 25.0;

        // Test Celsius -> Fahrenheit -> Celsius
        double roundTripC = TemperatureConverter.fahrenheitToCelsius(
            TemperatureConverter.celsiusToFahrenheit(originalCelsius)
        );
        assertEquals(roundTripC, originalCelsius, DELTA,
            "Round trip through Fahrenheit failed");

        // Test Celsius -> Kelvin -> Celsius
        roundTripC = TemperatureConverter.kelvinToCelsius(
            TemperatureConverter.celsiusToKelvin(originalCelsius)
        );
        assertEquals(roundTripC, originalCelsius, DELTA,
            "Round trip through Kelvin failed");
    }

    @DataProvider(name = "invalidTemperatures")
    public Object[][] getInvalidTemperatures() {
        return new Object[][] {
            {-274.0},
            {-300.0},
            {-500.0}
        };
    }

    @Test(dataProvider = "invalidTemperatures", groups = "validation")
    public void testInvalidTemperature(double kelvin) {
        assertFalse(TemperatureConverter.isValidTemperature(kelvin),
            "Temperature below absolute zero should be invalid: " + kelvin);
    }

    @Test(groups = "bugs")
    public void testBugInCelsiusToKelvin() {
        double testCelsius = 0.0; // freezing point
        double expectedKelvin = 273.15;
        double actualKelvin = TemperatureConverter.celsiusToKelvin(testCelsius);

        // This test will fail due to the bug
        assertEquals(actualKelvin, expectedKelvin, DELTA,
            String.format("Bug detected: Expected %.2fK but got %.2fK. " +
                          "The celsiusToKelvin method is subtracting the Kelvin constant " +
                          "instead of adding it.", expectedKelvin, actualKelvin));
    }

    @Test(groups = {"complexConversions", "roundTrip"})
    public void testMethodChaining() {
        double originalCelsius = 50.0;

        // Convert Celsius -> Kelvin -> Fahrenheit -> Celsius
        double result = TemperatureConverter.fahrenheitToCelsius(
            TemperatureConverter.kelvinToFahrenheit(
                TemperatureConverter.celsiusToKelvin(originalCelsius)
            )
        );

        assertEquals(result, originalCelsius, DELTA,
            "Method chaining conversion failed");
    }

    @Test(groups = "edgeCases")
    public void testHighTemperatures() {
        double veryCelsius = 1_000_000.0;
        assertTrue(TemperatureConverter.celsiusToFahrenheit(veryCelsius) > 0,
            "Very high temperature conversion failed");
    }

    @Test(groups = "edgeCases")
    public void testNearAbsoluteZero() {
        double nearlyCelsius = -273.14; // Just above absolute zero
        assertTrue(TemperatureConverter.isValidTemperature(
                TemperatureConverter.celsiusToKelvin(nearlyCelsius)),
            "Temperature near absolute zero should be valid");
    }

    @AfterSuite
    public void tearDown() {
        System.out.println("Completed Temperature Converter TestNG tests");
    }
}