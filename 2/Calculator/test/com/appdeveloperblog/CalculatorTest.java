package com.appdeveloperblog;

import org.junit.jupiter.api.*;

import javax.swing.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Test math operation in Calculator class")
class CalculatorTest {

    Calculator calculator;

    @BeforeAll
    static void setup(){
        System.out.println("executing beforeAll");
    }

    @BeforeEach
    void beforeEachTestMethod(){
        calculator = new Calculator();
        System.out.println("executing beforeEach");
    }

    @AfterEach
    void afterEachTestMethod(){
        System.out.println("executing afterEach");
    }

    @AfterAll
    static void cleanup(){
        System.out.println("executing cleanup");
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

    @Test
    @DisplayName("Test 5-2=3")
    void integerSubtraction(){
        System.out.println("test 5-2");
        int minuend = 5;
        int subtrahend = 2;
        int expectedResult = 3;

        int actualResult = calculator.integerSubtraction(minuend,subtrahend);

        assertEquals(expectedResult, actualResult,
                () -> minuend + "-" + subtrahend + " did not produce " + expectedResult);
    }


}


/*
Lifecycle:
@BeforeAll -> @BeforeEach -> TEST -> @AfterEach -> NEXT TEST
-> @BeforeEach -> TEST -> @AfterEach -> @AfterAll



 */