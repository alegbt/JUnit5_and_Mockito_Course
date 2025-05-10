package com.appsdeveloperblog.estore.service;

import com.appsdeveloperblog.estore.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UserServiceTest {

    String firstName;
    String lastName;
    String email;
    String password;
    String repeatPassword;
    UserService userService;

    @BeforeEach
    void init(){
         userService = new UserServiceImpl();
         firstName = "ale";
         lastName = "pino";
         email = "ale@gmail";
         password = "123";
         repeatPassword = "123";
    }


    @DisplayName("User object created")
    @Test
    void testCreateUser_whenUserDetailsProvided_returnUserObject(){
        // Arrange
        // Act
        User user = userService.createUser(firstName, lastName, email, password, repeatPassword);

        // Assert
        assertNotNull(user, "createUser should not return null");
        assertEquals(firstName, user.getFirstName(), "user's firstname is incorrect");
        assertEquals(lastName, user.getLastName(), "user's lastname is incorrect");
        assertEquals(email, user.getEmail(), "user's email is incorrect");
        assertNotNull(user.getId(), "user id is missing");
    }


    @DisplayName("Empty first name causes correct exception")
    @Test
    void testCreateUser_whenFirstNameIsEmpty_throwIllegalArgumentException(){
        // Arrange
        String firstName = "";
        String expectedErrorMessage = "User's firstname is empty";

        // Act & Assert
        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> {
        User user = userService.createUser(firstName, lastName, email, password, repeatPassword);
        }, "Empty first name should have caused illegal argument exception");

        assertEquals(expectedErrorMessage, thrown.getMessage() , "incorrect exception thrown");
    }


    @Test
    void testCreateUser_whenLastNameIsEmpty_throwIllegalArgumentException(){
        // Arrange
        String lastName = "";
        String expectedErrorMessage = "User's lastname is empty";

        // Act
        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            User user = userService.createUser(firstName, lastName, email, password, repeatPassword);
        }, "Empty lastname should have caused exception");

        // Assert
        assertEquals(expectedErrorMessage, thrown.getMessage(), "incorrect exception");
    }




}
