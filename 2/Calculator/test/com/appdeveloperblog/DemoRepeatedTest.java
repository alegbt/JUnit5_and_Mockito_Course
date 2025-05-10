package com.appdeveloperblog;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DemoRepeatedTest {

    Calculator calculator;

    @BeforeEach
    void beforeEachTestMethod(){
        calculator = new Calculator();
        System.out.println("executing @beforeEach");
    }



    @DisplayName("Division by 0")
    @RepeatedTest(value = 3, name = "{displayName}. Repetition {currentRepetition} of {totalRepetitions}")
    void testIntegerDivision_WhenDividendIsDividedByZero_ShouldThrowArithmeticException(
            RepetitionInfo repetitionInfo,
            TestInfo testInfo
    ) {
        System.out.println("Running " + testInfo.getTestMethod().get().getName());
        System.out.println("Repetition # " + repetitionInfo.getCurrentRepetition() + " of " + repetitionInfo.getTotalRepetitions());

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




}
