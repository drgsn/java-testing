# Date & Temperature Converter TestNG Module

This module demonstrates the implementation of test cases using TestNG framework for the Date/Time Converter and Temperature Converter utilities.

## Comparison between JUnit 5 and TestNG

### Annotation Changes
* JUnit 5's `@BeforeAll`/`@AfterAll` → TestNG's `@BeforeSuite`/`@AfterSuite`
* JUnit 5's `@BeforeEach`/`@AfterEach` → TestNG's `@BeforeMethod`/`@AfterMethod`
* JUnit 5's `@DisplayName` → TestNG's `description` attribute in `@Test`
* JUnit 5's `@ParameterizedTest` → TestNG's `@Test` with `@DataProvider`

### Test Configuration
* TestNG uses `@Test(groups="...")` for test grouping instead of JUnit's `@Nested` classes
* TestNG has built-in parallel execution support
* TestNG's timeouts are specified using `@Test(timeOut=...)` instead of JUnit's `@Timeout`

### Assertions
* TestNG doesn't have `assertAll`, so multiple assertions are done separately
* TestNG's assertions have slightly different parameter ordering
* TestNG's soft assertions require explicit `SoftAssert` class usage

### Data Providers
* TestNG uses `@DataProvider` instead of JUnit's various parameter sources
* Data providers in TestNG are more flexible and can return `Object[][]`

## Key Advantages of Using TestNG over JUnit 5

### 1. More Flexible Test Configuration
* Detailed XML configuration for test suites
* Better support for test groups and dependencies
* More control over test method execution order

### 2. Built-in Parallel Execution
* Can run tests in parallel at suite, test, class, or method level
* Better control over thread counts and parallel execution strategies

### 3. Data Providers
* More powerful data-driven testing capabilities
* Can return complex data structures
* Support for parallel data provider execution

### 4. Dependencies
* Better support for test dependencies with `@Test(dependsOnMethods=...)` or `@Test(dependsOnGroups=...)`
* Can configure soft dependencies that don't fail the entire suite

### 5. Test Grouping
* More flexible group definitions and execution
* Can include/exclude groups at runtime
* Better support for categorizing tests

## Project Structure

```
date-convertor-testng/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/
│   │           └── itadviser/
│   │               ├── DateTimeConverter.java
│   │               └── TemperatureConverter.java
│   └── test/
│       ├── java/
│       │   └── com/
│       │       └── itadviser/
│       │           ├── DateTimeConverterTestNG.java
│       │           └── TemperatureConverterTestNG.java
│       └── resources/
│           └── testng.xml
└── build.gradle
```

## Getting Started

1. Add the module to your project's `settings.gradle`:
```gradle
include 'date-convertor-testng'
```

2. Build configuration (`build.gradle`):
```gradle
plugins {
    id 'java'
}

dependencies {
    implementation 'joda-time:joda-time:2.12.7'
    testImplementation 'org.testng:testng:7.8.0'
}

test {
    useTestNG()
}
```

3. Run the tests:
```bash
./gradlew test
```

## XML Configuration

TestNG provides powerful test configuration through XML. Here's an example of our test suite configuration:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE suite SYSTEM "https://testng.org/testng-1.0.dtd">
<suite name="Date and Temperature Converter Test Suite">
    <test name="DateTimeConverter Tests">
        <classes>
            <class name="com.itadviser.DateTimeConverterTestNG"/>
        </classes>
    </test>
    
    <test name="TemperatureConverter Tests">
        <groups>
            <run>
                <include name="basicConversions"/>
                <include name="parameterized"/>
            </run>
        </groups>
        <classes>
            <class name="com.itadviser.TemperatureConverterTestNG"/>
        </classes>
    </test>
</suite>
```

## Known Issues

1. DateTimeConverter has a bug in nanosecond conversion:
    * The `toJavaDateTime` method uses incorrect multiplier for milliseconds to nanoseconds conversion
    * Affects all tests involving precise time conversions

2. TemperatureConverter has a bug in Celsius to Kelvin conversion:
    * The `celsiusToKelvin` method subtracts the Kelvin constant instead of adding it
    * Affects all temperature conversions involving Kelvin
