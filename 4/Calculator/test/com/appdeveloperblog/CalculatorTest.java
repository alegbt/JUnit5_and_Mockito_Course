package com.appdeveloperblog;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Test math operation in Calculator class")
class CalculatorTest {

    Calculator calculator;

    @BeforeAll
    static void setup(){
        System.out.println("executing @BeforeAll");
    }

    @BeforeEach
    void beforeEachTestMethod(){
        calculator = new Calculator();
        System.out.println("executing @BeforeEach");
    }

    @AfterEach
    void afterEachTestMethod(){
        System.out.println("executing @AfterEach");
    }

    @AfterAll
    static void cleanup(){
        System.out.println("executing @AfterAll");
    }


    //Naming convention
    // test<System Under Test>_<Condition or State Change>_<Expected Result>
    @Test
    @DisplayName("Test 4/2 = 2")
    void testIntegerDivision_WhenFourIsDividedByTwo_ShouldReturnTwo() {
        System.out.println("running tst 4/2");

        // Arrange // Given - (given certain infos)
        int dividend = 4;
        int divisor = 2;
        int expectedResult = 2;

        // Act  // When - (when doing certain operations)
        int actualResult = calculator.integerDivision(dividend,divisor);

        // Assert // Then - (then the following should be true)
        assertEquals(expectedResult, actualResult, "integerDivision custom error msg");
    }

    //@Disabled("Not implemented")
    @Test
    @DisplayName("Test division by 0")
    void testIntegerDivision_WhenDividendIsDividedByZero_ShouldTrowhArithmeticException() {
        System.out.println("running divide by 0");

        // Arrange
        int dividend = 4;
        int divisor = 0;
        String expectedExceptionMessage = "/ by zero";

        // Act
        ArithmeticException actualException = assertThrows(ArithmeticException.class, () ->{
            calculator.integerDivision(dividend, divisor);
        }, "should have thrown arithmetic exception");

        // Assert
        assertEquals(expectedExceptionMessage, actualException.getMessage(), "Unexpected exception occured");
    }


    @ParameterizedTest
    @ValueSource(strings = {"pino", "turbo", "due"}) //testa per ogni value (1 test x ognuno)
    void valueSourceDemonstration(String firstName){
        System.out.println(firstName);
        assertNotNull(firstName);
    }









    @DisplayName("Test intg subtraction [minuend, subtrahend, expectedResult]")
    @ParameterizedTest
    //@MethodSource() // mettendo una funzione con lo stesso nome di qst test posso passare 1 stream di value come params
//    @CsvSource({    //cosi passo delle value direttamente come param
//            "33, 1, 32",
//            "24, 1, 23",
//            "54, 1, 53"
//    })
    @CsvFileSource(resources = "/integerSubtraction.csv") // cosi passo 1 file da cui attingere i valori
    void integerSubtraction(int minuend, int subtrahend, int expectedResult){
        System.out.println("test " + minuend + " - " + subtrahend + " = "+ expectedResult);

        int actualResult = calculator.integerSubtraction(minuend,subtrahend);

        assertEquals(expectedResult, actualResult,
                () -> minuend + "-" + subtrahend + " did not produce " + expectedResult);
    }


    private static Stream<Arguments> integerSubtraction(){
        return Stream.of(
                Arguments.of(5,2,3),
                Arguments.of(54,1,53),
                Arguments.of(53,2,51)
        );
    }


}


/*
Lifecycle:
@BeforeAll -> @BeforeEach -> TEST -> @AfterEach -> NEXT TEST
-> @BeforeEach -> TEST -> @AfterEach -> @AfterAll



 */