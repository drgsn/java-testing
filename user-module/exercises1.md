## Integration tests exercises:

### for bonus points use:

- junit annotation to setup your tests context (@BeforeAll, @BeforeEach, @AfterEach, @AfterAll)
- sql script to insert data into the test db:
```java
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class YourTest {
    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest")
        .withInitScript("init-data.sql"); // SQL file in src/test/resources

    // Your tests
}
```

### Exercises

- Following the principles of incremental testing add tests for: 
 
1. Test update user successful.
2. Test email uniqueness constraints
3. Test update conflicts

- Following the principles of big bang integration testing add tests for: 

1. user deletion success
2. user deletion failure, user does not exist.
