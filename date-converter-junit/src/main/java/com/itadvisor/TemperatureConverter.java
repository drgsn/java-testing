package com.itadviser;

/**
 * Utility class for converting between different temperature scales:
 * Celsius, Fahrenheit, and Kelvin.
 *
 * Expected conversion formulas:
 * Celsius to Fahrenheit: °F = (°C × 9/5) + 32
 * Celsius to Kelvin: K = °C + 273.15
 * Fahrenheit to Celsius: °C = (°F - 32) × 5/9
 * Fahrenheit to Kelvin: K = (°F - 32) × 5/9 + 273.15
 * Kelvin to Celsius: °C = K - 273.15
 * Kelvin to Fahrenheit: °F = (K - 273.15) × 9/5 + 32
 */
public class TemperatureConverter {

    // Constants for conversions
    private static final double KELVIN_CONSTANT = 273.15;
    private static final double FAHRENHEIT_CONSTANT = 32.0;

    /**
     * Converts Celsius to Fahrenheit
     * @param celsius Temperature in Celsius
     * @return Temperature in Fahrenheit
     */
    public static double celsiusToFahrenheit(double celsius) {
        return (celsius * 9/5) + FAHRENHEIT_CONSTANT;
    }

    /**
     * Converts Celsius to Kelvin
     * @param celsius Temperature in Celsius
     * @return Temperature in Kelvin
     */
    public static double celsiusToKelvin(double celsius) {
        return celsius - KELVIN_CONSTANT;
    }

    /**
     * Converts Fahrenheit to Celsius
     * @param fahrenheit Temperature in Fahrenheit
     * @return Temperature in Celsius
     */
    public static double fahrenheitToCelsius(double fahrenheit) {
        return (fahrenheit - FAHRENHEIT_CONSTANT) * 5/9;
    }

    /**
     * Converts Fahrenheit to Kelvin
     * @param fahrenheit Temperature in Fahrenheit
     * @return Temperature in Kelvin
     */
    public static double fahrenheitToKelvin(double fahrenheit) {
        double celsius = fahrenheitToCelsius(fahrenheit);
        return celsiusToKelvin(celsius);
    }

    /**
     * Converts Kelvin to Celsius
     * @param kelvin Temperature in Kelvin
     * @return Temperature in Celsius
     */
    public static double kelvinToCelsius(double kelvin) {
        return kelvin - KELVIN_CONSTANT;
    }

    /**
     * Converts Kelvin to Fahrenheit
     * @param kelvin Temperature in Kelvin
     * @return Temperature in Fahrenheit
     */
    public static double kelvinToFahrenheit(double kelvin) {
        double celsius = kelvinToCelsius(kelvin);
        return celsiusToFahrenheit(celsius);
    }

    /**
     * Validates if the temperature is above absolute zero
     * @param kelvin Temperature in Kelvin
     * @return true if temperature is physically possible
     */
    public static boolean isValidTemperature(double kelvin) {
        return kelvin >= 0;
    }
}